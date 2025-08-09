SUMMARY = "Install hwrevision and fw_env.configfile to /etc of rescue image"
LICENSE = "CLOSED"

SRC_URI = "file://fw_env.config \
	   file://hwrevision"

PACKAGES = "${PN}"

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    install -m 0644 ${WORKDIR}/hwrevision ${D}${sysconfdir}/hwrevision
    sed -i -e "s,@MACHINE@,${MACHINE},g" ${D}${sysconfdir}/hwrevision
}

