FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'file://add-wpa-supplicant.cfg', '', d)}"

do_configure:append() {
    cat ../add-wpa-supplicant.cfg >> ${B}/wpa_supplicant/.config
}