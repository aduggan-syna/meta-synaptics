DESCRIPTION = "Inclusion of hardware revision and fw_env.config"
LICENSE = "CLOSED"

SRC_URI = "file://hwrevision \
           file://fw_env_sl1620.config \
           file://fw_env_sl1640.config \
           file://fw_env_sl1680.config \
           file://fw_env_sl2619.config \
           file://fw_env_rescue.config \
           file://www"

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/hwrevision ${D}${sysconfdir}/hwrevision
    if [ "${ENABLE_RESCUE_MODE}" = "1" ]; then
        install -m 0644 ${WORKDIR}/fw_env_rescue.config ${D}${sysconfdir}/fw_env.config
    else
        install -m 0644 ${WORKDIR}/fw_env_${MACHINE}.config ${D}${sysconfdir}/fw_env.config
    fi
    install -d ${D}${sysconfdir}/www
    cp -r ${WORKDIR}/www/* ${D}${sysconfdir}/www/
    sed -i -e 's,@MACHINE@,'${MACHINE}',g' \
        ${D}${sysconfdir}/hwrevision
}
