from PyQt5.QtWidgets import (
    QApplication, QWidget, QListWidget, QListWidgetItem, QLabel, QPushButton,
    QVBoxLayout, QHBoxLayout, QLineEdit, QRadioButton, QWidgetItem
)
from PyQt5.QtCore import Qt, QSize
import json, os
import sys

SAVED_PASSWORD_FILE = os.path.expanduser("/etc/.wifi_saved_password")

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
        if ssid in self.saved_password_dict:
            del self.saved_password_dict[ssid]
            save_password_file(self.saved_password_dict)
            self.load_ssids()

if __name__ == "__main__":
    if "QT_QPA_PLATFORM" not in os.environ:
        os.environ["QT_QPA_PLATFORM"] = "wayland"
    app = QApplication(sys.argv)
    with open("/usr/bin/style.qss", "r") as f:
        app.setStyleSheet(f.read())
    window = WiFiManager()
    window.show()
    sys.exit(app.exec_())

