DESCRIPTION = "Inclusion of hardware revision and fw_env.config"
LICENSE = "CLOSED"

inherit systemd

PACKAGES = "${PN}"

SRC_URI = "file://hwrevision \
           file://fw_env.config \
           file://www \
           file://swupdate-flag.service"

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/hwrevision ${D}${sysconfdir}/hwrevision
    install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    install -d ${D}${sysconfdir}/www
    cp -r ${WORKDIR}/www/* ${D}${sysconfdir}/www/
    sed -i -e 's,@MACHINE@,'${MACHINE}',g' \
        ${D}${sysconfdir}/hwrevision

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/swupdate-flag.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "${systemd_system_unitdir}/swupdate-flag.service"

# Skip QA warnings about FHS non-standard paths
INSANE_SKIP:${PN} += "installed-vs-shipped"

# Enable systemd service
SYSTEMD_SERVICE:${PN} = "swupdate-flag.service"
