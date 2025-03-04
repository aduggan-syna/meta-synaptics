do_configure:append() {
    echo 'CONFIG_IEEE80211AX=y' >> ${B}/hostapd/.config
    echo 'CONFIG_SAE=y' >> ${B}/hostapd/.config
    echo 'CONFIG_WPA3_SAE=y' >> ${B}/hostapd/.config
    echo 'CONFIG_6G=y' >> ${B}/hostapd/.config
    echo 'CONFIG_HE_SU=y' >> ${B}/hostapd/.config
    echo 'CONFIG_EHT=y' >> ${B}/hostapd/.config
    echo "rs.log bbappend"
}