FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " file://android-tools-adbd.service"

# Add simg2simg needed by image_synaimg
TOOLS_TO_BUILD:append:class-native = " simg2simg"

do_install:append:class-native() {
    install -D -p -m0755 ${S}/debian/out/system/core/simg2simg ${D}${bindir}/simg2simg
}

RDEPENDS:${BPN} = "p7zip"
RDEPENDS:${PN}-adbd = "${PN}-conf-configfs"

SYSTEMD_PACKAGES = "${PN}-adbd"
