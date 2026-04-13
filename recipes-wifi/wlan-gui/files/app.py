from PyQt5.QtWidgets import (
    QApplication, QWidget, QListWidget, QListWidgetItem, QLabel, QPushButton,
    QVBoxLayout, QHBoxLayout, QLineEdit, QRadioButton, QSizePolicy
)
from PyQt5.QtCore import Qt, QSize, QTimer
import scan_wifi
import json, os
import connection
from connection import flush_ipv6
import re
import sys
import subprocess
import time
import shutil

CONFIG_DIR = "/etc"
AUTO_CONNECT_FILE = os.path.join(CONFIG_DIR, "wifi_autoconnect")
SAVED_PASSWORD_FILE = os.path.join(CONFIG_DIR, "wifi_saved_password")
USAGE_FILE = os.path.join(CONFIG_DIR, "wifi_usage")

def load_usage():
    return json.load(open(USAGE_FILE, 'r')) if os.path.exists(USAGE_FILE) else {}

def load_saved_password_file():
    return json.load(open(SAVED_PASSWORD_FILE, 'r')) if os.path.exists(SAVED_PASSWORD_FILE) else {}

def get_connected_ssid():
    try:
        result = subprocess.run(["iw", "dev", "wlan0", "link"], capture_output=True, text=True)
        for line in result.stdout.split("\n"):
            if "SSID" in line:
                return line.replace("SSID: ", "").strip()
    except Exception as e:
        print(f"Error fetching SSID: {e}")
    return ""

def bring_up_wlan0():
    try:
        subprocess.run(["ifconfig", "wlan0", "up"], stderr=subprocess.STDOUT, text=True)
        subprocess.run(["systemctl", "start", "wpa_supplicant@wlan0.service"], stderr=subprocess.STDOUT, text=True)
    except subprocess.CalledProcessError as e:
        print(f"Error: {e.output}")

def check_command(command, output):
    if output:
        print(f"Command '{command}' output: {output}")

def run_command(command):
    result = subprocess.run(command, shell=True, capture_output=True, text=True)
    check_command(command, result.stderr)
    return result.stdout.strip() if result.stdout else ""

def remove_wpa_supplicant():
    wpa_supplicant_path = "/etc/wpa_supplicant"
    if os.path.exists(wpa_supplicant_path):
        try:
            shutil.rmtree(wpa_supplicant_path)
        except Exception as e:
            print(f"Error removing {wpa_supplicant_path}: {e}")
    else:
        print(f"{wpa_supplicant_path} does not exist.")

def check_wpa_supplicant_status():
    """Checks if wpa_supplicant@wlan0.service is enabled."""
    status = run_command("systemctl is-enabled wpa_supplicant@wlan0.service")
    return status

def enable_wpa_supplicant():
    """Enables wpa_supplicant@wlan0.service if it's disabled."""
    status = check_wpa_supplicant_status()
    if status == "disabled":
        enable_output = run_command("systemctl enable wpa_supplicant@wlan0.service")
    elif status == "enabled":
        pass
    else:
        print(f"Error checking service status: {status}")

class WiFiManager(QWidget):
    def __init__(self, parent=None):
        super(WiFiManager, self).__init__(parent)
        self.setWindowTitle("Available Networks")
        self.setFixedSize(450, 650)

        self.heading_label = QLabel("Select SSID")
        self.heading_label.setAlignment(Qt.AlignCenter)

        self.search_box = QLineEdit()
        self.search_box.setPlaceholderText("Search SSIDs...")
        self.search_box.textChanged.connect(self.filter_list)

        self.menu_widget = QListWidget()
        self.menu_widget.itemClicked.connect(self.open_wifi_setup)

        layout = QVBoxLayout()
        layout.addWidget(self.heading_label)
        layout.addWidget(self.search_box)
        layout.addWidget(self.menu_widget)

        self.password_input = QLineEdit()

        self.setLayout(layout)
        self.load_ssids()

        self.timer = QTimer(self)
        self.timer.timeout.connect(self.load_ssids)
        self.timer.start(15000)

    def resume_scanning(self):
        if not self.timer.isActive():
            self.timer.start(15000)

    def load_ssids(self):
        bring_up_wlan0()
        self.menu_widget.clear()
        self.ssid_ret = scan_wifi.scan_ssids()
        self.ssid_list = self.ssid_ret[0]
        self.ssid_dict = self.ssid_ret[1]
        connected_ssid = get_connected_ssid()
        usage = load_usage()
        self.ssid_list.sort(key=lambda x: -usage.get(x, 0))
        if connected_ssid in self.ssid_list:
            self.ssid_list.remove(connected_ssid)
            self.ssid_list.insert(0, f"✔ {connected_ssid} (Connected)")
        for ssid in self.ssid_list:
            item = QListWidgetItem(ssid)
            item.setSizeHint(QSize(250, 70))
            self.menu_widget.addItem(item)

    def refresh_ssids(self, status=None):
        self.menu_widget.clear()
        connected_ssid = get_connected_ssid()
        if status == "Disconnect":
            first_ssid = self.ssid_list[0]
            inx = first_ssid.find("(")
            first_ssid = first_ssid[2:inx-1]
            self.ssid_list[0] = first_ssid
        for ssid in self.ssid_list:
            item = QListWidgetItem(ssid)
            item.setSizeHint(QSize(250, 70))
            self.menu_widget.addItem(item)

    def refresh_ssids_connect(self, selected_ssid):
        self.menu_widget.clear()
        connected_ssid = get_connected_ssid()
        first_ssid = self.ssid_list[0]
        if "Connect" in first_ssid:
            inx = first_ssid.find("(")
            first_ssid = first_ssid[2:inx-1]
            self.ssid_list[0] = first_ssid

        self.ssid_list.remove(selected_ssid)
        self.ssid_list.insert(0, f"✔ {selected_ssid} (Connected)")
        for ssid in self.ssid_list:
            item = QListWidgetItem(ssid)
            item.setSizeHint(QSize(250, 70))
            self.menu_widget.addItem(item)

    def filter_list(self):
        search_text = self.search_box.text().lower()
        self.menu_widget.clear()
        for ssid in self.ssid_list:
            if search_text in ssid.lower():
                item = QListWidgetItem(ssid)
                item.setSizeHint(QSize(250, 70))
                self.menu_widget.addItem(item)

    def open_wifi_setup(self, item):
        if self.timer.isActive():
            self.timer.stop()
        currentItem = item.text().replace("✔ ", "").split(" (Connected)")[0]
        self.selected_ssid = currentItem
        connected_ssid = get_connected_ssid()
        if currentItem == connected_ssid:
            widget = QWidget()
            layout = QHBoxLayout()
            ssid_label = QLabel(currentItem)
            ssid_label.setStyleSheet("color: white;")
            disconnect_button = QPushButton("Disconnect")
            disconnect_button.setFixedSize(140, 47)
            disconnect_button.clicked.connect(self.disconnect_functionality)

            layout.addWidget(ssid_label)
            layout.addWidget(disconnect_button)
            layout.setContentsMargins(5, 2, 5, 2)

            widget.setLayout(layout)
            item.setSizeHint(QSize(250, 70))
            self.menu_widget.setItemWidget(item, widget)
        else:
            # Attempt connection using saved password
            saved_password_dict = load_saved_password_file()
            if currentItem in saved_password_dict:
                self.psk = saved_password_dict[currentItem]
                connection_status = connection.wifi_connection(self.selected_ssid, self.psk, self.ssid_dict[self.selected_ssid], self.password_input.text())
                if not connection_status:
                    self.layout().addWidget(QLabel("*Failed to connect. Check saved password."))
                else:
                    self.increment_wifi_usage(currentItem)
                    self.refresh_ssids_connect(currentItem)
            else:
                # Ask for password if not saved
                self.selected_ssid = currentItem
                self.setup_window = QWidget()
                self.setup_window.setWindowTitle(f"{self.selected_ssid}")
                self.setup_window.setFixedSize(500, 300)
                self.layout_outside = QVBoxLayout()

                self.password_input = QLineEdit()
                self.password_input.setPlaceholderText("Enter Password")
                self.password_input.setEchoMode(QLineEdit.Password)
                self.password_input.setVisible(False)
                self.layout_outside.addWidget(self.password_input)

                self.auto_connect_radio = QRadioButton("Enable Auto-Connect")
                self.auto_connect_radio.setChecked(True)
                self.auto_connect_radio.setVisible(False)
                self.layout_outside.addWidget(self.auto_connect_radio)

                connected_ssid = get_connected_ssid()
                saved_password_dict = load_saved_password_file()

                if connected_ssid == self.selected_ssid:
                    self.disconnect_button = QPushButton("Disconnect")
                    self.disconnect_button.clicked.connect(self.disconnect_functionality)
                    self.layout_outside.addWidget(self.disconnect_button)
                else:
                    self.connect_button = QPushButton("Connect")

                    if self.selected_ssid in saved_password_dict:
                        self.connect_button.clicked.connect(self.connect_to_wifi)
                    else:
                        self.connect_button.clicked.connect(self.toggle_password_field)

                    self.layout_outside.addWidget(self.connect_button)

                self.setup_window.setLayout(self.layout_outside)
                self.setup_window.show()

    def toggle_password_field(self):
        if not self.password_input.isVisible():
            self.password_input.setVisible(True)
            self.auto_connect_radio.setVisible(True)
            return

        if len(self.password_input.text()) < 8 and len(self.password_input.text()) != 0:
            error_label = QLabel("* Password must be at least 8 characters")
            error_label.setStyleSheet("color: white;")
            self.layout_outside.addWidget(error_label)
            return

        self.connect_to_wifi()

    def increment_wifi_usage(self, currentItem=None):
        ssid_frequency = load_usage()
        def freq_increment(current_ssid):
            if current_ssid in ssid_frequency:
                ssid_frequency[current_ssid] += 1
            else:
                ssid_frequency[current_ssid] = 1
            self.save_usage(ssid_frequency)
        if currentItem:
            freq_increment(currentItem)
        else:
            freq_increment(self.selected_ssid)

    def connect_to_wifi(self):
        saved_password_dict = load_saved_password_file()
        if self.selected_ssid in saved_password_dict:
            self.psk = saved_password_dict[self.selected_ssid]
            if 'SAE' in self.ssid_dict[self.selected_ssid]:
                connection_status = connection.wifi_connection(self.selected_ssid, self.password_input.text(), self.ssid_dict[self.selected_ssid], self.password_input.text())
            else:
                connection_status = connection.wifi_connection(self.selected_ssid, self.psk, self.ssid_dict[self.selected_ssid], self.password_input.text())
            if not connection_status:
                self.layout_outside.addWidget(QLabel("*Failed to connect. Check saved password."))
            else:
                self.setup_window.close()
                self.increment_wifi_usage()
                self.refresh_ssids_connect(self.selected_ssid)
            return
        else:
            def extract_psk():
                if self.password_input.text() == "":
                    return ""
                def check_command(success, message):
                    if not success:
                        print(f"Error: {message} failed.")
                        return False
                try:
                    result = subprocess.run(
                        ["wpa_passphrase", self.selected_ssid, self.password_input.text()],
                        text=True,
                        capture_output=True,
                        check=True
                    )
                    wpa_output = result.stdout
                    check_command(True, "wpa_passphrase generation")
                except subprocess.CalledProcessError:
                    check_command(False, "wpa_passphrase generation")
                match = re.search(r"^\s*psk=(.+)$", wpa_output, re.MULTILINE)
                if match:
                    psk = match.group(1)
                else:
                    print("Error: Failed to extract PSK.")
                    sys.exit(1)
                return psk
            self.psk = extract_psk()
            if self.psk is False:
                return
        if 'SAE' in self.ssid_dict[self.selected_ssid] and 'PSK' not in self.ssid_dict[self.selected_ssid]:
            connection_status = connection.wifi_connection(self.selected_ssid, self.password_input.text(), self.ssid_dict[self.selected_ssid], self.password_input.text())
        else:
            connection_status = connection.wifi_connection(self.selected_ssid, self.psk, self.ssid_dict[self.selected_ssid], self.password_input.text())
        if not connection_status:
            error_label_pass = QLabel("* Password is incorrect")
            error_label_pass.setStyleSheet("color: white;")
            self.layout_outside.addWidget(error_label_pass)
            return

        if self.auto_connect_radio.isChecked():
            with open(AUTO_CONNECT_FILE, "w") as file:
                if 'SAE' in self.ssid_dict[self.selected_ssid]:
                    file.write(f"{self.selected_ssid}:{self.password_input.text()}\n")
                else:
                    file.write(f"{self.selected_ssid}:{self.psk}\n")

        self.refresh_ssids_connect(self.selected_ssid)
        self.increment_wifi_usage()
        if 'SAE' in self.ssid_dict[self.selected_ssid]:
            saved_password_dict[self.selected_ssid] = self.password_input.text()
        else:
            saved_password_dict[self.selected_ssid] = self.psk
        self.save_password(saved_password_dict)
        self.setup_window.close()
        self.resume_scanning()

    def save_usage(self, usage):
        """Save connection frequency data to a file."""
        with open(USAGE_FILE, 'w') as f:
            json.dump(usage, f, indent=4)

    def save_password(self, saved_password_dict):
        with open(SAVED_PASSWORD_FILE, 'w') as f:
            json.dump(saved_password_dict, f, indent=4)

    def disconnect_functionality(self):
        subprocess.run(["killall", "udhcpc"])
        subprocess.run(["killall", "wpa_supplicant"])
        subprocess.run(["systemctl", "stop", "wpa_supplicant@wlan0.service"])

        flush_ipv6("wlan0")

        remove_wpa_supplicant()
        time.sleep(3)
        self.refresh_ssids("Disconnect")
        if os.path.isfile(AUTO_CONNECT_FILE):
            try:
                os.remove(AUTO_CONNECT_FILE)
            except Exception as e:
                print(f"Error processing file: {e}")
        subprocess.run(["systemctl", "start", "wpa_supplicant@wlan0.service"])
        saved_password_dict = load_saved_password_file()
        self.save_password(saved_password_dict)


if __name__ == "__main__":
    result = subprocess.run(["systemctl", "enable", "wlan_start.service"], check=True, text=True, capture_output=True)
    result = subprocess.run(["systemctl", "start", "dns.service"], check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    subprocess.run("echo 1 > /sys/class/rfkill/rfkill1/state", shell=True)
    bring_up_wlan0()
    if "QT_QPA_PLATFORM" not in os.environ:
        os.environ["QT_QPA_PLATFORM"] = "wayland"

    enable_wpa_supplicant()

    app = QApplication(sys.argv)
    with open("/usr/bin/style.qss", "r") as f:
        app.setStyleSheet(f.read())
    window = WiFiManager()
    window.show()
    sys.exit(app.exec_())
