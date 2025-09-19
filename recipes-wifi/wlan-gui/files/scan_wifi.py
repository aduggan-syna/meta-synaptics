import subprocess
import re

def scan_ssids():
    try:
        # Run the iw command to scan Wi-Fi networks
        result = subprocess.run(['iw', 'dev', 'wlan0', 'scan'], capture_output=True, text=True)
        result = result.stdout
        ssids = set()
        ssid_dict = {}
        bss_blocks = result.split("BSS ")
        for line in bss_blocks:
            # Look for SSID lines
            match = re.search(r'SSID: (.+)', line)
            if match:
                ssid = match.group(1).strip()
                ssids.add(ssid)
                if re.search(r'RSN:\s+\* Version: 1', line):
                    auth_match = re.search(r'Authentication suites: (.+)', line)
                    auth = auth_match.group(1).strip()
                    if auth == "PSK SAE":
                        auth = "PSK"
                else:
                    auth = ""
                ssid_dict[ssid] = auth
        return [list(ssids), ssid_dict]
    except Exception as e:
        print(f"Error scanning SSIDs: {e}")
        return [[], {}]
