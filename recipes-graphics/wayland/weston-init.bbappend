FILESEXTRAPATHS:append := "${THISDIR}/weston-init:"

SRC_URI += "file://71-weston-drm.rules"
SRC_URI += "file://weston.env"
SRC_URI += "file://weston.ini"
SRC_URI += "file://weston.service"
SRC_URI += "file://weston-start"
SRC_URI += "file://99-touch-mapping-sl1620.rules"
SRC_URI += "file://99-touch-mapping-sl1680.rules"

inherit update-alternatives
ALTERNATIVE:${PN} = "weston_ini"
ALTERNATIVE_LINK_NAME[weston_ini] = "/etc/xdg/weston/weston.ini"
ALTERNATIVE_PATH[weston_ini] = "${D}${sysconfdir}/xdg/weston/weston.ini"
ALTERNATIVE_PRIORITY[weston_ini] = "100"

do_install:append() {
	install -D -p -m0644 ${WORKDIR}/71-weston-drm.rules \
		${D}${sysconfdir}/udev/rules.d/71-weston-drm.rules

	RULE=""
	if echo "${MACHINE}" | grep -Eq "^sl1620"; then
		RULE="99-touch-mapping-sl1620.rules"
	elif echo "${MACHINE}" | grep -Eq "^sl1680"; then
		RULE="99-touch-mapping-sl1680.rules"
	fi

	if [ -n "${RULE}" ]; then
		install -Dm0644 ${WORKDIR}/${RULE} \
		${D}${sysconfdir}/udev/rules.d/99-touch-mapping.rules
	fi

	# Install weston-start script
	install -Dm755 ${WORKDIR}/weston-start ${D}${bindir}/weston-start
	sed -i 's,@DATADIR@,${datadir},g' ${D}${bindir}/weston-start
	sed -i 's,@LOCALSTATEDIR@,${localstatedir},g' ${D}${bindir}/weston-start
}

REQUIRED_DISTRO_FEATURES:remove = "${@oe.utils.conditional('VIRTUAL-RUNTIME_init_manager', 'systemd', 'pam', '', d)}"
