DESCRIPTION = "Inclusion of hardware revision and fw_env.config"
LICENSE = "CLOSED"

SRC_URI = "file://hwrevision \
           file://fw_env.config \
           file://www"

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/hwrevision ${D}${sysconfdir}/hwrevision
    install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    install -d ${D}${sysconfdir}/www
    cp -r ${WORKDIR}/www/* ${D}${sysconfdir}/www/
    sed -i -e 's,@MACHINE@,'${MACHINE}',g' \
        ${D}${sysconfdir}/hwrevision
}
