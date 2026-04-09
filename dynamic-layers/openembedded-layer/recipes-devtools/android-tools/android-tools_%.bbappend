FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

inherit update-rc.d

SRC_URI += " file://android-tools-adbd.service \
             file://0006-change-pthread_kill-for-glibc.patch \
             file://android-tools-adbd.sh"

# Add simg2simg needed by image_synaimg
TOOLS_TO_BUILD:append:class-native = " simg2simg"

do_install:append:class-native() {
    install -D -p -m0755 ${S}/debian/out/system/core/simg2simg ${D}${bindir}/simg2simg
}

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -D -p -m0755 ${WORKDIR}/android-tools-adbd.sh ${D}${sysconfdir}/init.d/android-tools-adbd
    fi
}

RDEPENDS:${BPN} = "p7zip"
RDEPENDS:${PN}-adbd = "${PN}-conf-configfs"

SYSTEMD_PACKAGES = "${PN}-adbd"

INITSCRIPT_NAME = "android-tools-adbd"
INITSCRIPT_PACKAGES = "${PN}"
