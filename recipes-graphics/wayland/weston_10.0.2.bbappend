# Incorporate IMG changes into Weston
FILESEXTRAPATHS:append := "${THISDIR}/weston:"

SRC_URI += "file://0003-libweston-reduce-checks-for-dmabufs-with-DRM-modifie.patch"
SRC_URI += "file://0005-backend-drm-allow-linear-framebuffers-if-no-KMS-modi.patch"
SRC_URI += "file://0006-enable-triple-buffer.patch"
SRC_URI += "file://0007-xdgwindow-select-display.patch"
SRC_URI += "file://0008-update-drm-planes-on-disconnect.patch"
SRC_URI += "file://0009-enable-leaseout-drm-resources.patch"

PACKAGECONFIG:append:class-target = " wl_shell"
PACKAGECONFIG[wl_shell] = "-Ddeprecated-wl-shell=true,-Ddeprecated-wl-shell=false"

REQUIRED_DISTRO_FEATURES:remove = "${@oe.utils.conditional('VIRTUAL-RUNTIME_init_manager', 'systemd', 'pam', '', d)}"
