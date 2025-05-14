from PyQt5.QtWidgets import (
    QApplication, QWidget, QListWidget, QListWidgetItem, QLabel, QPushButton,
    QVBoxLayout, QHBoxLayout, QLineEdit
)
from PyQt5.QtCore import Qt, QSize, QThread, pyqtSignal
import scan_devices
import os
import sys
import time
from collections import defaultdict
import test_connection
import subprocess


def run_bluetoothctl(commands, wait_time=5):
    process = subprocess.Popen(
        ['bluetoothctl'],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )
    output = []

    for cmd in commands:
        process.stdin.write(cmd + '\n')
        process.stdin.flush()
        time.sleep(1)

    time.sleep(wait_time)
    process.stdin.write('exit\n')
    process.stdin.flush()
    output, _ = process.communicate()
    return output.splitlines()


bt_mac_addr = {}


class DeviceInfo:
    def __init__(self, mac, name):
        self.mac = mac
        self.name = name

    def __hash__(self):
        return hash(self.mac)

    def __eq__(self, other):
        return isinstance(other, DeviceInfo) and self.mac == other.mac


class Devices(QThread):
    update_signal = pyqtSignal(str)

    devices_st = set()

    def run(self):
        while True:
            time.sleep(1)
            dev_list = scan_devices.scan_new_devices()
            for mac_addr, devices in dev_list:
                if mac_addr not in self.devices_st:
                    self.update_signal.emit(devices + "#" + mac_addr)
                    self.devices_st.add(mac_addr)


class BluetoothScanner(QThread):
    def run(self):
        run_bluetoothctl(["power on"])
        while True:
            run_bluetoothctl(["scan on"], wait_time=10)
            time.sleep(10)


class ConnectionMonitor(QThread):
    connection_update_signal = pyqtSignal(str, bool)

    def __init__(self, mac_list_func):
        super().__init__()
        self.get_macs = mac_list_func
        self._running = True

    def run(self):
        while self._running:
            for mac in self.get_macs():
                is_connected = test_connection.is_device_connected(mac)
                self.connection_update_signal.emit(mac, is_connected)
            time.sleep(5)

    def stop(self):
        self._running = False


class BTManager(QWidget):
    def __init__(self, parent=None):
        super(BTManager, self).__init__(parent)
        self.setWindowTitle("Add Devices")
        self.setFixedSize(600, 800)

        self.heading_label = QLabel("Select Device")
        self.heading_label.setAlignment(Qt.AlignCenter)

        self.menu_widget = QListWidget()
        self.menu_widget.itemClicked.connect(self.connect_to_bt_device)

        layout = QVBoxLayout()
        layout.addWidget(self.heading_label)
        layout.addWidget(self.menu_widget)

        self.setLayout(layout)
        self.mac_to_device = defaultdict(QListWidgetItem)
        self.current_connected_item = None
        self.item_widgets = {}

        self.load_devices()

        self.scanner_thread = BluetoothScanner()
        self.scanner_thread.start()

        self.connection_monitor = ConnectionMonitor(self.get_all_mac_addrs)
        self.connection_monitor.connection_update_signal.connect(self.update_device_connection_status)
        self.connection_monitor.start()

    def load_devices(self):
        self.device = Devices()
        self.device.update_signal.connect(self.add_device_to_list)
        self.device.start()

    def add_device_to_list(self, device_details):
        inx = device_details.find("#")
        device_name = device_details[:inx]
        mac_addr = device_details[inx + 1:]

        if mac_addr in [self.mac_to_device[id(item)] for item in self.get_all_items()]:
            return

        item = QListWidgetItem()
        item.setSizeHint(QSize(250, 70))
        self.mac_to_device[id(item)] = mac_addr

        widget = QWidget()
        layout = QHBoxLayout()
        layout.setContentsMargins(10, 5, 10, 5)

        dev_label = QLabel(device_name)
        dev_label.setStyleSheet("color: white; font-size: 14pt;")

        disconnect_button = QPushButton("Disconnect")
        disconnect_button.setFixedSize(140, 47)
        disconnect_button.clicked.connect(lambda _, m=mac_addr, i=item: self.disconnect_bt_device(m, i))

        layout.addWidget(dev_label)
        layout.addStretch()
        layout.addWidget(disconnect_button)
        widget.setLayout(layout)

        item_data = {
            "label": dev_label,
            "button": disconnect_button,
            "widget": widget,
        }
        item.setData(Qt.UserRole, item_data)

        if test_connection.is_device_connected(mac_addr):
            dev_label.setText(device_name + " (connected)")
            disconnect_button.setVisible(True)
            self.menu_widget.insertItem(0, item)
            self.current_connected_item = item
        else:
            disconnect_button.setVisible(False)
            self.menu_widget.addItem(item)

        self.menu_widget.setItemWidget(item, widget)

    def get_all_items(self):
        return [self.menu_widget.item(i) for i in range(self.menu_widget.count())]

    def get_all_mac_addrs(self):
        return list(self.mac_to_device.values())

    def update_device_connection_status(self, mac, is_connected):
        for item in self.get_all_items():
            if self.mac_to_device.get(id(item)) == mac:
                data = item.data(Qt.UserRole)
                label = data["label"]
                button = data["button"]

                if is_connected:
                    if "(connected)" not in label.text():
                        label.setText(label.text() + " (connected)")
                    button.setVisible(True)
                else:
                    if "(connected)" in label.text():
                        label.setText(label.text().replace(" (connected)", ""))
                    button.setVisible(False)

    def connect_to_bt_device(self, item):
        new_mac_addr = self.mac_to_device[id(item)]

        if self.current_connected_item is not None:
            prev_item = self.current_connected_item
            prev_mac = self.mac_to_device[id(prev_item)]

            if prev_mac != new_mac_addr:
                print(f"Disconnecting {prev_mac}")
                test_connection.disconnect_device(prev_mac)

                prev_data = prev_item.data(Qt.UserRole)
                prev_data["label"].setText(prev_data["label"].text().replace(" (connected)", ""))
                prev_data["button"].setVisible(False)
                self.current_connected_item = None

        if test_connection.auto_connect_headset(new_mac_addr):
            print("Connected to", new_mac_addr)

            old_data = item.data(Qt.UserRole)
            device_name = old_data["label"].text()
            if "(connected)" not in device_name:
                device_name += " (connected)"

            new_item = QListWidgetItem()
            new_item.setSizeHint(QSize(250, 70))
            self.mac_to_device[id(new_item)] = new_mac_addr

            new_label = QLabel(device_name)
            new_label.setStyleSheet("color: white;")

            new_button = QPushButton("Disconnect")
            new_button.setFixedSize(140, 47)
            new_button.setVisible(True)
            new_button.clicked.connect(lambda _, m=new_mac_addr, i=new_item: self.disconnect_bt_device(m, i))

            layout = QHBoxLayout()
            layout.setContentsMargins(10, 5, 10, 5)
            layout.addWidget(new_label)
            layout.addStretch()
            layout.addWidget(new_button)

            widget = QWidget()
            widget.setLayout(layout)

            new_item.setData(Qt.UserRole, {
                "label": new_label,
                "button": new_button,
                "widget": widget
            })

            row = self.menu_widget.row(item)
            self.menu_widget.takeItem(row)
            self.menu_widget.insertItem(0, new_item)
            self.menu_widget.setItemWidget(new_item, widget)

            self.current_connected_item = new_item
        else:
            print("Failed to connect to", new_mac_addr)

    def disconnect_bt_device(self, mac_addr, item):
        print(f"Disconnecting device: {mac_addr}")
        if test_connection.disconnect_device(mac_addr):
            data = item.data(Qt.UserRole)
            data["label"].setText(data["label"].text().replace(" (connected)", ""))
            data["button"].setVisible(False)
            self.current_connected_item = None

    def closeEvent(self, event):
        self.connection_monitor.stop()
        super().closeEvent(event)


if __name__ == "__main__":
    if "QT_QPA_PLATFORM" not in os.environ:
        os.environ["QT_QPA_PLATFORM"] = "wayland"

    try:
        subprocess.run(["systemctl", "enable", "bt_auto.service"], check=True)
        subprocess.run(["systemctl", "start", "bt_auto.service"], check=True)
    except subprocess.CalledProcessError as e:
        print(f"Failed to start or enable bt_auto.service: {e}")

    app = QApplication(sys.argv)
    with open("/usr/bin/style.qss", "r") as f:
        app.setStyleSheet(f.read())
    window = BTManager()
    window.show()
    sys.exit(app.exec_())

