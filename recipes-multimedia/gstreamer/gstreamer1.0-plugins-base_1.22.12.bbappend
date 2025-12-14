FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
PACKAGECONFIG:remove:sl2611 = "opengl"
PACKAGECONFIG:remove:sl2611 = "egl"
PACKAGECONFIG:remove:sl2611 = "gl"
PACKAGECONFIG:remove:sl2611 = "gles2"
PACKAGECONFIG:remove:sl2611 = "wayland"

SRC_URI:append = " \
    file://0001-video-color-normalize-colorimetry-to-string-result.patch;striplevel=3 \
    "
