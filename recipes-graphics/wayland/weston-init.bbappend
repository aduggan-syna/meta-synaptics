FILESEXTRAPATHS:append := "${THISDIR}/weston-init:"

SRC_URI += "file://71-weston-drm.rules"
SRC_URI += "file://weston.env"
SRC_URI += "file://weston.ini"
SRC_URI += "file://weston.service"
SRC_URI += "file://weston-start"

inherit update-alternatives
ALTERNATIVE:${PN} = "weston_ini"
ALTERNATIVE_LINK_NAME[weston_ini] = "/etc/xdg/weston/weston.ini"
ALTERNATIVE_PATH[weston_ini] = "${D}${sysconfdir}/xdg/weston/weston.ini"
ALTERNATIVE_PRIORITY[weston_ini] = "100"


do_install:append() {
	install -D -p -m0644 ${WORKDIR}/71-weston-drm.rules \
		${D}${sysconfdir}/udev/rules.d/71-weston-drm.rules
}

REQUIRED_DISTRO_FEATURES:remove = "${@oe.utils.conditional('VIRTUAL-RUNTIME_init_manager', 'systemd', 'pam', '', d)}"
