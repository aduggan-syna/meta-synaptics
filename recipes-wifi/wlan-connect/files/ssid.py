import subprocess
import re

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

def scan_ssids(interface):
    try:
        # Run the iw command to scan Wi-Fi networks, substituting the interface
        result = subprocess.run(['iw', 'dev', interface, 'scan'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, timeout=10)

        if result.returncode != 0:
            print(f"Error running iw command: {result.stderr}")
            return []

        ssids = []
        for line in result.stdout.splitlines():
            # Look for SSID lines
            match = re.search(r'SSID: (.+)', line)
            if match:
                ssid = match.group(1).strip()
                ssids.append(ssid)
        return ssids
    except Exception as e:
        print(f"Error scanning SSIDs: {e}")
        return []

def display_ssids(ssids):
    if ssids:
        print("Available Networks:")
        for idx, ssid in enumerate(ssids, 1):
            print(f"{idx}. {ssid}")
    else:
        print("No networks found.")

if __name__ == "__main__":
    interface = find_wireless_interface()

    if interface:
        print(f"Using interface: {interface}")  # Display the found interface
        networks = scan_ssids(interface)
        display_ssids(networks)
    else:
        print("Error: No wireless interface found.")
