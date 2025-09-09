import subprocess
import time

def run_bluetoothctl(commands):
    """Run commands inside bluetoothctl and capture output"""
    process = subprocess.Popen(['bluetoothctl'], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    output = []

    for cmd in commands:
        process.stdin.write(cmd + '\n')
        process.stdin.flush()
        time.sleep(1)

    process.stdin.write('exit\n')
    process.stdin.flush()
    output, _ = process.communicate()
    return output.splitlines()

def scan_new_devices():
    """Scan for Bluetooth devices and return list of (MAC, Name)"""
    print("Scanning for Bluetooth devices...")

    output = run_bluetoothctl(["devices"])
    devices = []
    for line in output:
        if line.strip().startswith("Device"):
            parts = line.strip().split(" ", 2)
            if len(parts) == 3:
                mac, name = parts[1], parts[2]
                devices.append((mac, name))
    return devices

def get_paired_devices():
    try:
        result = subprocess.run(
            "grep -iH 'name' /var/lib/bluetooth/*/*/info",
            shell=True, text=True, capture_output=True
        )

        output_lines = result.stdout.strip().split("\n")
        paired_devices = []

        for line in output_lines:
            if not line.strip():
                continue

            parts = line.rsplit(":", 1)
            if len(parts) < 2:
                print("Skipping line, no colon found:", line)
                continue

            path, key_value = parts
            path_parts = path.strip("/").split("/")

            if len(path_parts) < 5:
                print("Skipping line, path parts too short:", path_parts)
                continue

            mac_addr = path_parts[-2]

            if "=" in key_value:
                device_name = key_value.split("=", 1)[1].strip()
            else:
                device_name = ""

            paired_devices.append((mac_addr, device_name))

        print("Paired devices:", paired_devices)
        return paired_devices

    except Exception as e:
        print(f"Error reading paired devices: {e}")
        return []
