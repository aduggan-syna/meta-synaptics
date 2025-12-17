DESCRIPTION = "Synaptics brcm bt start service"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"
PR = "r0"

inherit systemd

COMPATIBLE_MACHINE = "platypus|dolphin|myna2|klamath"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

RDEPENDS:${PN} += " \
    brcm-patchram-plus \
    linux-firmware-syna \
"

SRC_URI:append = " \
    file://brcm_bt_start.service \
"

SRC_URI:append:dolphin = " \
    file://dolphin_brcm_bt_start.patch \
"

SRC_URI:append:myna2 = " \
    file://myna2_brcm_bt_start.patch \
"

SRC_URI:append:klamath = " \
    file://klamath_brcm_bt_start.patch \
"

do_patch(){
    case "${MACHINE}" in
        sl1680)
            cd ${WORKDIR}
            patch -p1 < dolphin_brcm_bt_start.patch
            ;;
        sl1620)
            cd ${WORKDIR}
            patch -p1 < myna2_brcm_bt_start.patch
            ;;
        sl2619|sl2615|sl2611|klamath)
            cd ${WORKDIR}
            patch -p1 < klamath_brcm_bt_start.patch
            ;;
    esac
}

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/brcm_bt_start.service ${D}${systemd_system_unitdir}
}

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'brcm_bt_start.service', '', d)}"
FILES:${PN} += " \
    ${systemd_system_unitdir} \
"

