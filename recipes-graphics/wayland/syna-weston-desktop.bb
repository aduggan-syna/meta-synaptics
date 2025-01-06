DESCRIPTION = "WESTON-SYNA-DESKTOP"
SECTION = "weston"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

inherit meson pkgconfig update-alternatives

DEPENDS += "wayland wayland-native wayland-protocols weston libinput libdrm libwebp"

SRC_URI = "${SYNA_SRC_DEMOS}"
S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/demos/syna-weston-desktop"
SRCREV = "${SYNA_SRCREV_DEMOS}"

ALTERNATIVE:${PN} = "weston_ini"
ALTERNATIVE_LINK_NAME[weston_ini] = "/etc/xdg/weston/weston.ini"
ALTERNATIVE_PATH[weston_ini] = "${D}${sysconfdir}/xdg/weston/weston.ini"
ALTERNATIVE_PRIORITY[weston_ini] = "200"

FILES:${PN} += "/usr/lib/syna-desktop-shell.so"

do_install:append() {
    install -d ${D}${sysconfdir}/xdg/weston/
    cp -r ${S}/weston-${MACHINE}.ini ${D}${sysconfdir}/xdg/weston/weston.ini
}
