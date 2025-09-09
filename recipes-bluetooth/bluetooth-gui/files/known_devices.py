from PyQt5.QtWidgets import (
    QApplication, QWidget, QListWidget, QListWidgetItem, QLabel, QPushButton,
    QVBoxLayout, QHBoxLayout
)
from PyQt5.QtCore import QSize
import os
import sys
import scan_devices
import test_connection


class BLManager(QWidget):
    def __init__(self, parent=None):
        super(BLManager, self).__init__(parent)
        self.setWindowTitle("Manage Known Devices")
        self.setFixedSize(450, 650)

        self.menu_widget = QListWidget()

        layout = QVBoxLayout()
        layout.addWidget(self.menu_widget)

        self.setLayout(layout)
        self.load_devices()

    def load_devices(self):
        self.menu_widget.clear()
        saved_devices = scan_devices.get_paired_devices()
        device_names = [b for a,b in saved_devices]
        mac_addr_dict = {b:a for a,b in saved_devices}

        for device in device_names:
            widget = QWidget()
            h_layout = QHBoxLayout()

            ssid_label = QLabel(device)
            ssid_label.setStyleSheet("color: white; font-size: 14pt;")

            forget_button = QPushButton("Remove")
            forget_button.setFixedSize(140, 47)
            forget_button.clicked.connect(lambda _, s=device: self.remove_devices(mac_addr_dict[s]))

            h_layout.addWidget(ssid_label)
            h_layout.addWidget(forget_button)
            h_layout.setContentsMargins(10, 5, 10, 5)

            widget.setLayout(h_layout)
            item = QListWidgetItem()
            item.setSizeHint(QSize(250, 70))

            self.menu_widget.addItem(item)
            self.menu_widget.setItemWidget(item, widget)

    def remove_devices(self, mac_addr):
        test_connection.remove_device(mac_addr)
        self.load_devices()

if __name__ == "__main__":
    if "QT_QPA_PLATFORM" not in os.environ:
        os.environ["QT_QPA_PLATFORM"] = "wayland"
    app = QApplication(sys.argv)
    with open("/usr/bin/style.qss", "r") as f:
        app.setStyleSheet(f.read())
    window = BLManager()
    window.show()
    sys.exit(app.exec_())
