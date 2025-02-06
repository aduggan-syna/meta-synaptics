import subprocess
import re
import os
import json

# File to track connection frequency
USAGE_FILE = os.path.expanduser("/etc/.wifi_usage")
def load_usage():
    """Load connection frequency data from a file."""
    if os.path.exists(USAGE_FILE):
        with open(USAGE_FILE, 'r') as f:
            return json.load(f)
    return {}

def find_wireless_interface():
    try:
        result = subprocess.run(['iw', 'dev'], capture_output=True, text=True, check=True)
        output = result.stdout

        # Iterate over each line in the output of 'iw dev'
        for line in output.splitlines():
            line = line.strip()  # Strip any leading/trailing spaces

            # Check if the line starts with "Interface" and extract the interface name
            if line.startswith('Interface'):
                interface = line.split()[1]  # The interface name is the second word
                return interface

        return None  # Return None if no interface is found

    except subprocess.CalledProcessError:
        print("Error: Failed to run 'iw dev' command.")
        return None

def scan_ssids():
    """Scan for available Wi-Fi networks."""
    try:
        # Run the iw command to scan Wi-Fi networks
        result = subprocess.run(['iw', 'dev', 'wlan0', 'scan'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, timeout=10)

        if result.returncode != 0:
            print(f"Error running iw command: {result.stderr}")
            return []

        ssids = set()
        for line in result.stdout.splitlines():
            # Look for SSID lines
            match = re.search(r'SSID: (.+)', line)
            if match:
                ssid = match.group(1).strip()
                ssids.add(ssid)
        usage = load_usage()
        ssids = list(ssids)
        sorted_ssids = sorted(ssids, key=lambda x: -usage.get(x,0))
        return sorted_ssids
    except Exception as e:
        print(f"Error scanning SSIDs: {e}")
        return []

if __name__ == "__main__":
    interface = find_wireless_interface()

    if interface:
        print(f"Using interface: {interface}")  # Display the found interface
        networks = scan_ssids()
    else:
        print("Error: No wireless interface found.")
