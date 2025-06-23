FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"

SRC_URI:append = " \
    file://0001-video-color-normalize-colorimetry-to-string-result.patch;striplevel=3 \
    "
