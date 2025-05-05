# Incorporate IMG patches for Xorg server
FILESEXTRAPATHS:append := "${THISDIR}/xserver-xorg:"


SRC_URI += " \
    file://0001-glamor-use-dual-source-blend-on-GL-2.1-with-ARB_ES2_.patch \
    file://0002-glamor-handle-EXT_gpu_shader4-in-dual-source-blend-p.patch \
    file://0003-present-don-t-do-a-vblank-abort-on-a-pending-flip-du.patch \
    file://0004-glamor-improve-OpenGLES-support.patch \
    file://0005-glamor-if-no-DRI2-don-t-call-DRI2CloseScreen.patch \
    file://0006-glamor-add-glsl-shaders-for-OpenGLES-3.0.patch \
    file://0007-glamor-fix-SW-cursor-handling-for-IMG-OpenGLES3.patch \
    file://0008-modesetting-add-option-to-not-render-a-SW-cursor.patch \
    file://0009-meson-create-xfree86-log-directory.patch \
    file://0010-glx-allow-direct-GLX-with-DRI3-only-screens.patch \
    file://0011-glamor-increase-the-precision-for-some-GLES-shader-t.patch \
    file://0012-glx-only-support-32-bit-pixmaps-pbuffers-with-Glamor.patch \
    file://0013-Revert-render-Break-PICT_a4.patch \
    file://0014-config-Fix-bus-ID-of-platform-device-with-PCI-parent.patch \
    file://0015-meson-pkg-config-was-not-being-used-for-libcrypto.patch \
    file://0016-glamor-add-alpha_dual_blend-program-for-OpenGLES3.patch \
    file://0017-glamor-xv-add-glsl-shaders-for-OpenGLES-3.0.patch \
"

INSANE_SKIP:${PN} += "empty-dirs"

FILES:${PN} += " \
    /var \
    /var/log \
"

# changes to pass custom xorg config file for Xserver
SRC_URI += "file://xorg_conf_override"
SRC_URI += "file://xorg_conf"

do_install:append() {
    mkdir -p ${D}${sysconfdir}/X11
    install -m 0755 ${WORKDIR}/xorg_conf_override ${D}${sysconfdir}/X11/xserver-common
    install -m 0755 ${WORKDIR}/xorg_conf ${D}${sysconfdir}/X11/xorg.conf
}
