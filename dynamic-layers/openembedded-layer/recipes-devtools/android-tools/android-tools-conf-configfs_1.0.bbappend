FILESEXTRAPATHS:prepend := "${THISDIR}/android-tools-conf-configfs:"

SRC_URI += " \
    file://android-gadget-setup \
    file://android-gadget-start \
    file://android-gadget-cleanup \
"
