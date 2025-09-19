from PyQt5.QtWidgets import (
    QApplication, QWidget, QListWidget, QListWidgetItem, QLabel, QPushButton,
    QVBoxLayout, QHBoxLayout, QLineEdit, QRadioButton, QWidgetItem
)
from PyQt5.QtCore import Qt, QSize
import json, os
import sys
import subprocess
import time
import shutil

SAVED_PASSWORD_FILE = os.path.expanduser("/etc/wifi_saved_password")
AUTO_CONNECT_FILE = os.path.expanduser("/etc/wifi_autoconnect")

def load_saved_password_file():
    return json.load(open(SAVED_PASSWORD_FILE, 'r')) if os.path.exists(SAVED_PASSWORD_FILE) else {}

def save_password_file(data):
    with open(SAVED_PASSWORD_FILE, 'w') as f:
        json.dump(data, f, indent=4)

class WiFiManager(QWidget):
    def __init__(self, parent=None):
        super(WiFiManager, self).__init__(parent)
        self.setWindowTitle("Manage Known Networks")
        self.setFixedSize(400, 600)

        self.search_box = QLineEdit()
        self.search_box.setPlaceholderText("Search known networks...")
        self.search_box.textChanged.connect(self.filter_list)

        self.menu_widget = QListWidget()

        layout = QVBoxLayout()
        layout.addWidget(self.search_box)
        layout.addWidget(self.menu_widget)

        self.setLayout(layout)
        self.load_ssids()
        connected_ssid = self.get_connected_ssid()

    def get_connected_ssid(self):
        try:
            output = subprocess.check_output(["iw", "dev", "wlan0", "link"], text=True)

            for line in output.splitlines():
                if line.strip().startswith("SSID:"):
                    ssid = line.split("SSID:")[1].strip()
                    return ssid

        except subprocess.CalledProcessError as e:
            print("[ERROR] Failed to run iw command:", e)

        return None

    def remove_wpa_supplicant(self):
        wpa_supplicant_path = "/etc/wpa_supplicant"

        if os.path.exists(wpa_supplicant_path):
            try:
                shutil.rmtree(wpa_supplicant_path)
            except Exception as e:
                print(f"Error removing {wpa_supplicant_path}: {e}")
        else:
            print(f"{wpa_supplicant_path} does not exist.")

    def save_password(self,saved_password_dict):
        with open(SAVED_PASSWORD_FILE, 'w') as f:
            json.dump(saved_password_dict, f, indent=4)

    def load_ssids(self):
        self.menu_widget.clear()
        self.saved_password_dict = load_saved_password_file()
        saved_ssids = self.saved_password_dict.keys()

        for ssid in saved_ssids:
            widget = QWidget()
            h_layout = QHBoxLayout()

            ssid_label = QLabel(ssid)
            ssid_label.setStyleSheet("color: white;")

            forget_button = QPushButton("Forget")
            forget_button.setFixedSize(140, 47)
            forget_button.clicked.connect(lambda _, s=ssid: self.forget_ssid(s))

            h_layout.addWidget(ssid_label)
            h_layout.addWidget(forget_button)
            h_layout.setContentsMargins(10, 5, 10, 5)

            widget.setLayout(h_layout)
            item = QListWidgetItem()
            item.setSizeHint(QSize(250, 70))

            self.menu_widget.addItem(item)
            self.menu_widget.setItemWidget(item, widget)

    def filter_list(self):
        search_text = self.search_box.text().lower()
        self.menu_widget.clear()

        for ssid in self.saved_password_dict.keys():
            if search_text in ssid.lower():
                widget = QWidget()
                h_layout = QHBoxLayout()

                ssid_label = QLabel(ssid)
                forget_button = QPushButton("Forget")
                forget_button.setFixedSize(90, 50)
                forget_button.clicked.connect(lambda _, s=ssid: self.forget_ssid(s))

                h_layout.addWidget(ssid_label)
                h_layout.addWidget(forget_button)
                h_layout.setContentsMargins(10, 5, 10, 5)

                widget.setLayout(h_layout)
                item = QListWidgetItem()
                item.setSizeHint(widget.sizeHint())

                self.menu_widget.addItem(item)
                self.menu_widget.setItemWidget(item, widget)

    def forget_ssid(self, ssid):
        connected_ssid = self.get_connected_ssid()
        if connected_ssid and ssid.strip() == connected_ssid.strip():
            subprocess.run(["killall", "udhcpc"])
            subprocess.run(["killall", "wpa_supplicant"])
            subprocess.run(["systemctl", "stop", "wpa_supplicant@wlan0.service"])
            subprocess.run(["ip", "addr", "flush", "dev", "wlan0"])
            self.remove_wpa_supplicant()
            time.sleep(3)
            if os.path.isfile(AUTO_CONNECT_FILE):
                try:
                    os.remove(AUTO_CONNECT_FILE)
                except Exception as e:
                    print(f"Error processing file: {e}")
            subprocess.run(["systemctl", "start", "wpa_supplicant@wlan0.service"])
            saved_password_dict = load_saved_password_file()
            self.save_password(saved_password_dict)

        if ssid in self.saved_password_dict:
            del self.saved_password_dict[ssid]
            save_password_file(self.saved_password_dict)

        self.load_ssids()
        return True


if __name__ == "__main__":
    if "QT_QPA_PLATFORM" not in os.environ:
        os.environ["QT_QPA_PLATFORM"] = "wayland"
    app = QApplication(sys.argv)
    with open("/usr/bin/style.qss", "r") as f:
        app.setStyleSheet(f.read())
    window = WiFiManager()
    window.show()
    sys.exit(app.exec_())

