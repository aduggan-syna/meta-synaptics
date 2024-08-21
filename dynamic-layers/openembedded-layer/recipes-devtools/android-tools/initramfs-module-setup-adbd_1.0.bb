DESCRIPTION = "Setup adbd in initramfs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += " \
    file://adbd \
"

do_install(){
    install -d ${D}/init.d
    install -m 0755 ${WORKDIR}/adbd ${D}/init.d/00-adbd
}

FILES:${PN} = "/init.d/00-adbd"
