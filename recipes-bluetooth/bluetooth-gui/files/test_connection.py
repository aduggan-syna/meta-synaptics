import subprocess
import time
import pexpect

def run_bluetoothctl_cmd(cmd):
    process = subprocess.run(
        ['bluetoothctl'],
        input=cmd,
        capture_output=True,
        text=True
    )
    time.sleep(1)
    return process.stdout

def run_bluetoothctl(commands):
    """Run commands inside bluetoothctl and capture output"""
    process = subprocess.Popen(
        ['bluetoothctl'],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )
    output = []

    for cmd in commands:
        process.stdin.write(cmd + '\n')
        process.stdin.flush()
        time.sleep(1)

    process.stdin.write('exit\n')
    process.stdin.flush()
    output, _ = process.communicate()
    return output.splitlines()

def auto_connect_headset(HEADSET_MAC, timeout=30):
    try:
        try:
            info_output = subprocess.check_output(
            f"bluetoothctl info {HEADSET_MAC}", shell=True
            ).decode()
            is_paired = "paired: yes" in info_output.lower()
        except subprocess.CalledProcessError:
            is_paired = False

        try:
            connected_devices_output = subprocess.check_output(
                "bluetoothctl devices Connected", shell=True
            ).decode()
            for line in connected_devices_output.strip().split("\n"):
                if line.startswith("Device"):
                    parts = line.strip().split()
                    if len(parts) >= 2:
                        connected_mac = parts[1]
                        if connected_mac != HEADSET_MAC:
                            print(f"Disconnecting currently connected device: {connected_mac}")
                            disconnect_device(connected_mac)

        except subprocess.CalledProcessError:
            pass

        child = pexpect.spawn("bluetoothctl", encoding="utf-8", timeout=timeout)

        def send_cmd(cmd, wait=3):
            child.sendline(cmd)
            try:
                child.expect(["\\[bluetooth\\].*#", pexpect.TIMEOUT], timeout=wait)
            except pexpect.exceptions.TIMEOUT:
                pass

        send_cmd("power on")
        send_cmd("agent on")
        send_cmd("default-agent")

        if not is_paired:
            send_cmd(f"pair {HEADSET_MAC}")

            while True:
                index = child.expect([
                    r'Confirm passkey.*\(yes/no\)',
                    r'\[agent\] Confirm passkey.*\(yes/no\)',
                    "Pairing successful",
                    "Failed to pair",
                    pexpect.TIMEOUT
                ], timeout=10)

                if index in [0, 1]:
                    child.sendline("yes")
                    print("Sent 'yes' to confirm passkey.")
                    continue
                elif index == 2:
                    print("Pairing successful.")
                    break
                elif index == 3:
                    print("Failed to pair.")
                    return False
                elif index == 4:
                    print("Timeout during pairing.")
                    return False
        else:
            print("Device is already paired. Skipping pairing steps.")

        send_cmd(f"trust {HEADSET_MAC}")
        send_cmd(f"connect {HEADSET_MAC}")
        child.sendline("exit")
        child.close()

        for _ in range(5):
            try:
                output = subprocess.check_output("bluetoothctl devices Connected", shell=True).decode()
                print("Connected devices output:", output)
                if HEADSET_MAC in output:
                    print("connection_status: True")
                    return True
            except subprocess.CalledProcessError:
                pass
            time.sleep(1)

        print("connection_status: False")
        return False

    except Exception as e:
        print(f"Error in auto_connect_headset: {e}")
        return False

def get_connected_devices():
    try:
        output = subprocess.check_output("bluetoothctl info", shell=True).decode()
        lines = output.strip().split("\n")

        if lines and lines[0].startswith("Device"):
            parts = lines[0].split()
            if len(parts) >= 2:
                mac_addr = parts[1].strip()
                return mac_addr

        print("No device info found.")
        return []
    except Exception as e:
        print("Error fetching connected devices:", e)
        return []

def is_device_connected(mac_addr):
    try:
        output = subprocess.check_output(f"bluetoothctl info {mac_addr}", shell=True).decode()
        return "connected: yes" in output.lower()
    except subprocess.CalledProcessError:
        return False
    except Exception as e:
        print(f"Unexpected error checking connection for {mac_addr}: {e}")
        return False

def disconnect_device(mac_addr):
    try:
        output = subprocess.check_output(f"bluetoothctl disconnect {mac_addr}", shell=True).decode()
        print(f"Disconnected {mac_addr}")
        return "successful" in output.lower()
    except subprocess.CalledProcessError:
        print(f"Could not disconnect {mac_addr} maybe already disconnected.")
        return False

def remove_device(mac_addr):
    print("Removing device:", mac_addr)
    commands = [
        "power on",
        f"remove {mac_addr}"
    ]
    output = run_bluetoothctl(commands)
