import subprocess
import re

def scan_ssids():
    try:
        # Run the iw command to scan Wi-Fi networks
        result = subprocess.run(['iw', 'dev', 'wlan0', 'scan'], stdout=subprocess.PIPE, text=True)
        ssids = set()
        for line in result.stdout.splitlines():
            # Look for SSID lines
            match = re.search(r'SSID: (.+)', line)
            if match:
                ssid = match.group(1).strip()
                ssids.add(ssid)
        return list(ssids)
    except Exception as e:
        print(f"Error scanning SSIDs: {e}")
        return []
