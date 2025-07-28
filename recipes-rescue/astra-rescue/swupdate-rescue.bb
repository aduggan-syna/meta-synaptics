SUMMARY = "Install hwrevision, fw_env.config, swupdate.cfg file to /etc of rescue image"
LICENSE = "CLOSED"

SRC_URI = "file://fw_env.config \
	   file://hwrevision \
	   file://swupdate-wrapper \
	   file://swupdate.cfg.in"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/swupdate.cfg.in ${D}${sysconfdir}/swupdate.cfg
    install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    install -m 0644 ${WORKDIR}/hwrevision ${D}${sysconfdir}/hwrevision
    sed -i -e 's,@MACHINE@,${MACHINE},g' ${D}${sysconfdir}/hwrevision
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/swupdate-wrapper ${D}${bindir}/swupdate-wrapper
    ln -s ${bindir}/swupdate-wrapper ${D}${bindir}/update
}

FILES:${PN} += "${sysconfdir}/fw_env.config ${sysconfdir}/hwrevision ${sysconfdir}/swupdate.cfg"
