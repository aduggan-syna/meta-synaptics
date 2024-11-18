# Incorporate IMG patches for Xorg server
FILESEXTRAPATHS:append := "${THISDIR}/xserver-xorg:"

SRC_URI += "file://0001-present-don-t-do-a-vblank-abort-on-a-pending-flip-du.patch"
SRC_URI += "file://0002-glamor-improve-OpenGLES-support.patch"
SRC_URI += "file://0003-glamor-if-no-DRI2-don-t-call-DRI2CloseScreen.patch"
SRC_URI += "file://0004-glamor-add-glsl-shaders-for-OpenGLES-3.0.patch"
SRC_URI += "file://0005-glamor-fix-SW-cursor-handling-for-IMG-OpenGLES3.patch"
SRC_URI += "file://0006-modesetting-add-option-to-not-render-a-SW-cursor.patch"
#SRC_URI += "file://0007-meson-create-xfree86-log-directory.patch"
SRC_URI += "file://0008-glx-allow-direct-GLX-with-DRI3-only-screens.patch"
SRC_URI += "file://0009-glamor-increase-the-precision-for-some-GLES-shader-t.patch"
SRC_URI += "file://0010-glx-only-support-32-bit-pixmaps-pbuffers-with-Glamor.patch"
SRC_URI += "file://0011-Revert-render-Break-PICT_a4.patch"
SRC_URI += "file://0012-config-Fix-bus-ID-of-platform-device-with-PCI-parent.patch"
SRC_URI += "file://0013-meson-pkg-config-was-not-being-used-for-libcrypto.patch"

# changes to pass custom xorg config file for Xserver
SRC_URI += "file://xorg_conf_override"
SRC_URI += "file://xorg_conf"

XSERVER:append = " xf86-video-modesetting"

do_install:append() {
    mkdir -p ${D}${sysconfdir}/X11
    install -m 0755 ${WORKDIR}/xorg_conf_override ${D}${sysconfdir}/X11/xserver-common
    install -m 0755 ${WORKDIR}/xorg_conf ${D}${sysconfdir}/X11/xorg.conf
}
