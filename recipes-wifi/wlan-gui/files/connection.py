import os
import subprocess
import time

def check_command(command, output):
    if output:
        print(f"Command '{command}' output: {output}")

def run_command(command):
    result = subprocess.run(command, shell=True, capture_output=True, text=True)
    check_command(command, result.stderr)
    return result.stdout.strip() if result.stdout else ""

def is_authenticated():
    status_output = run_command("wpa_cli -i wlan0 status")
    return "wpa_state=COMPLETED" in status_output


def wifi_connection(ssid, psk, auth, password):
    run_command("ifconfig wlan0 up")
    run_command("killall udhcpc")
    run_command("killall wpa_supplicant")

    print("Creating the WPA Supplicant configuration directory...")
    os.makedirs("/etc/wpa_supplicant", exist_ok=True)

    result = subprocess.run("iw dev | awk '$1==\"Interface\"{print $2}' | head -n 1",
                            shell=True, capture_output=True, text=True)
    interface = result.stdout.strip()
    config_file = f"/etc/wpa_supplicant/wpa_supplicant-{interface}.conf"

    ssid_t = '"' + ssid + '"'
    if 'PSK' in auth or len(psk) == 0:
        if psk == "":
                config_content = f"""ctrl_interface=/var/run/wpa_supplicant
        ctrl_interface_group=0
        update_config=1


        network={{
            ssid={ssid_t}
            key_mgmt=NONE
            scan_ssid=1
        }}
        """
        else:
            config_content = f"""ctrl_interface=/var/run/wpa_supplicant
            ctrl_interface_group=0
    update_config=1


        network={{
            ssid={ssid_t}
            psk={psk}
            key_mgmt=WPA-PSK
            scan_ssid=1
        }}
        """
    else:
        config_content = f"""
            ctrl_interface=/var/run/wpa_supplicant
            ctrl_interface_group=0
            update_config=1

            network={{
                ssid={ssid_t}
                psk="{psk}"
                key_mgmt=SAE
                scan_ssid=1
                ieee80211w=2
            }}
        """
    print("config file: ", config_content)
    with open(config_file, "w") as f:
        f.write(config_content)

    print("Restarting the wpa_supplicant service...")
    run_command("systemctl restart wpa_supplicant@wlan0.service")
    retries = 0
    print(run_command("iw dev wlan0 link"))
    while not is_authenticated():
        if retries >= 5:
            print("Error: Failed to authenticate with the network.")
            return False
        print("Waiting for authentication...")
        time.sleep(4)
        retries += 1
    run_command("udhcpc -i wlan0")
    return True
