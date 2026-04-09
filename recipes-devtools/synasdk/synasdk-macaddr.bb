DESCRIPTION = "Synaptics eth0mac service"
SECTION = "devtools"
LICENSE = "CLOSED"
PR = "r0"

inherit systemd update-rc.d

SRC_URI = " \
   file://eth0mac \
   file://eth0mac.service \
   file://eth0mac.sh \
"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/eth0mac ${D}${sbindir}/eth0mac
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/eth0mac.service ${D}/${systemd_system_unitdir}/eth0mac.service
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -D -p -m0755 ${WORKDIR}/eth0mac.sh ${D}${sysconfdir}/init.d/eth0mac
        sed -i -e s:@sysconfdir@:${sysconfdir}:g \
               -e s:@sbindir@:${sbindir}:g \
                  ${D}${sysconfdir}/init.d/eth0mac
    fi
}

SYSTEMD_PACKAGES = "synasdk-macaddr"
SYSTEMD_SERVICE:synasdk-macaddr = "eth0mac.service"
INITSCRIPT_NAME = "eth0mac"
INITSCRIPT_PACKAGES = "synasdk-macaddr"
INITSCRIPT_PARAMS = "start 8 5 2 . stop 21 0 1 6 ."

FILES:${PN} = " \
    ${sbindir}/eth0mac \
    ${sysconfdir}/init.d/eth0mac \
    ${systemd_system_unitdir}/eth0mac.service \
"
