FILESEXTRAPATHS:prepend := "${THISDIR}/android-tools-conf-configfs:"

SRC_URI += " file://android-gadget-setup"

PROVIDES:remove = "android-tools-conf"
RPROVIDES:${PN}:remove = "android-tools-conf"
