DESCRIPTION = "Synaptics brcm bt start service"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"
PR = "r0"

inherit systemd update-rc.d

COMPATIBLE_MACHINE = "platypus|dolphin|myna2|klamath"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

RDEPENDS:${PN} += " \
    brcm-patchram-plus \
    linux-firmware-syna \
"

SRC_URI:append = " \
    file://brcm_bt_start.service \
    file://brcm_bt_start.sh \
"

btuart:myna2 = "/dev/ttyS1"
btuart:klamath = "/dev/ttyS1"
btuart:platypus = "/dev/ttyS2"
btuart:dolphin = "/dev/ttyS3"

do_install() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/brcm_bt_start.service ${D}${systemd_system_unitdir}
        sed -i -e s:@btuart@:${btuart}:g \
            ${D}${systemd_system_unitdir}/brcm_bt_start.service
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -D -p -m0755 ${WORKDIR}/brcm_bt_start.sh ${D}${sysconfdir}/init.d/brcm_bt_start
        sed -i -e s:@sysconfdir@:${sysconfdir}:g \
               -e s:@bindir@:${bindir}:g \
               -e s:@btuart@:${btuart}:g \
                  ${D}${sysconfdir}/init.d/brcm_bt_start
    fi
}

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'brcm_bt_start.service', '', d)}"
INITSCRIPT_NAME:${PN} = "brcm_bt_start"
INITSCRIPT_PACKAGES = "${PN}"
