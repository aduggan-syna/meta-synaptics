import sys
import re
import subprocess
import os
import time
from PyQt6.QtWidgets import QApplication, QWidget, QVBoxLayout, QListWidget, QPushButton, QListWidgetItem
from PyQt6.QtGui import QFont
from PyQt6.QtCore import Qt
 
 
class SSIDInfoApp(QWidget):
    def __init__(self):
        super().__init__()
 
        self.setWindowTitle("Network Info")
        self.setGeometry(300, 300, 600, 500)
 
        self.layout = QVBoxLayout()
 
        self.info_list = QListWidget()
        self.layout.addWidget(self.info_list)
 
        self.refresh_button = QPushButton("Refresh Now")
        self.refresh_button.clicked.connect(self.get_ssid_information)
        self.layout.addWidget(self.refresh_button)
 
        self.setLayout(self.layout)
 
        self.get_ssid_information()
 
    def add_heading(self, text):
        item = QListWidgetItem(text)
        item.setFont(QFont("Arial", 18, QFont.Bold))
        item.setTextAlignment(Qt.AlignCenter)
        self.info_list.addItem(item)
 
    def add_info(self, label, value):
        self.info_list.addItem(f"{label}: {value}")
 
    def get_ssid_information(self):
        self.info_list.clear()
        try:
            # Detect interface
            result = subprocess.run(["iw", "dev"], capture_output=True, text=True)
            interface_match = re.search(r"Interface\s+(\w+)", result.stdout)
            interface = interface_match.group(1) if interface_match else "Not Found"
 
            if interface == "Not Found":
                raise Exception("No Wi-Fi interface detected.")
 
            result = subprocess.run(f"iw dev {interface} link", shell=True, capture_output=True, text=True)
            if "Not connected." in result.stdout:
                self.info_list.addItem("Wi-Fi not connected")
                return
 
            # ifconfig output
            result = subprocess.run(["ifconfig", interface], capture_output=True, text=True)
            output = result.stdout
 
            mac_match = re.search(r"(?:ether|HWaddr)\s+([0-9A-Fa-f:]{17})", output)
            ipv4_match = re.search(r"inet\s(?:addr:)?([0-9.]+)", output)
            broadcast_match = re.search(r"Bcast:([0-9.]+)|broadcast\s+([0-9.]+)", output)
            mask_match = re.search(r"Mask:([0-9.]+)|netmask\s+([0-9.]+)", output)
            rx_bytes_match = re.search(r"RX\sbytes:([0-9]+)", output)
 
            mac_address = mac_match.group(1) if mac_match else "Not Found"
            ipv4_address = ipv4_match.group(1) if ipv4_match else None
            broadcast = broadcast_match.group(1) or broadcast_match.group(2) if broadcast_match else "Not Found"
            subnet_mask = mask_match.group(1) or mask_match.group(2) if mask_match else "Not Found"
            speed = calculate_rx_speed(interface)
 
            result = subprocess.run(["ip", "-6", "addr", "show", "dev", interface], capture_output=True, text=True)

            link_local_ipv6 = None
            global_ipv6_list = []

            for line in result.stdout.splitlines():
                line = line.strip()
                if line.startswith("inet6"):
                    match = re.match(r"inet6\s+([0-9a-f:]+)::?[0-9a-f:]*/\d+\s+scope\s+(\w+)", line)
                    if match:
                        addr, scope = match.groups()
                        if scope == "global":
                            global_ipv6_list.append(addr)
                        elif scope == "link":
                            link_local_ipv6 = addr
 
            result = subprocess.run(["iw", "dev", interface, "link"], capture_output=True, text=True)
            link_output = result.stdout
            ssid_match = re.search(r"SSID:\s(.+)", link_output)
            signal_match = re.search(r"signal:\s(-?\d+\s*dBm)", link_output)
            freq_match = re.search(r"freq:\s(\d+)", link_output)
 
            ssid = ssid_match.group(1) if ssid_match else "Not Found"
            signal = signal_match.group(1) if signal_match else "Not Found"
            freq = freq_match.group(1) + " MHz" if freq_match else "Not Found"
 
            # Routes
            result = subprocess.run(f"ip -6 route show default dev {interface}", shell=True, capture_output=True, text=True)
            ipv6_route = re.search(r"default via ([0-9a-f:]+)", result.stdout)
 
            result = subprocess.run(f"ip route show default dev {interface}", shell=True, capture_output=True, text=True)
            ipv4_route = re.search(r"default via ([0-9.]+)", result.stdout)
 
            default_route = ipv4_route.group(1) if ipv4_route else (ipv6_route.group(1) if ipv6_route else "Not Found")
 
            # DNS
            result = subprocess.run(["cat", "/etc/resolv.conf"], capture_output=True, text=True)
            dns_matches = re.findall(r"nameserver\s+(\S+)", result.stdout)
            primary_dns = dns_matches[0] if len(dns_matches) > 0 else "Not Found"
            secondary_dns = dns_matches[1] if len(dns_matches) > 1 else "Not Found"
 
            # Display
            self.add_heading("General")
            self.add_info("SSID", ssid)
            self.add_info("Interface", interface)
            self.add_info("MAC Address", mac_address)
            self.add_info("Speed", speed)
            self.add_info("Signal", signal)
            self.add_info("Frequency", freq)
 
            if ipv4_address:
                self.add_heading("IPv4")
                self.add_info("IP Address", ipv4_address)
                self.add_info("Broadcast Address", broadcast)
                self.add_info("Subnet Mask", subnet_mask)
                self.add_info("Default Route", default_route)
                self.add_info("Primary DNS", primary_dns)
                self.add_info("Secondary DNS", secondary_dns)

            if global_ipv6_list or link_local_ipv6:
                self.add_heading("IPv6")
                for i, addr in enumerate(global_ipv6_list, 1):
                    label = f"Global IPv6 Address {i}" if len(global_ipv6_list) > 1 else "Global IPv6 Address"
                    self.add_info(label, addr)
                if link_local_ipv6:
                    self.add_info("Link-Local IPv6 Address", link_local_ipv6)

        except Exception as e:
            self.info_list.clear()
            self.add_heading("Error")
            self.info_list.addItem(str(e))
 
def get_rx_bytes(interface):
    with open(f"/sys/class/net/{interface}/statistics/rx_bytes", "r") as f:
        return int(f.read().strip())
 
def calculate_rx_speed(interface, duration=1.0):
    rx1 = get_rx_bytes(interface)
    time.sleep(duration)
    rx2 = get_rx_bytes(interface)
    speed_bytes_per_sec = (rx2 - rx1) / duration
    speed_mbps = speed_bytes_per_sec * 8 / 1_000_000
    return f"{speed_mbps:.2f} Mbps"
 
if __name__ == "__main__":
    time.sleep(1)
    if "QT_QPA_PLATFORM" not in os.environ:
        os.environ["QT_QPA_PLATFORM"] = "wayland"
 
    app = QApplication(sys.argv)
    try:
        with open("/usr/bin/style.qss", "r") as f:
            app.setStyleSheet(f.read())
    except FileNotFoundError:
        pass
 
    window = SSIDInfoApp()
    window.show()
    sys.exit(app.exec_())

