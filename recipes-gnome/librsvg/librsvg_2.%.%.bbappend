PACKAGECONFIG:remove = "gdkpixbuf"
PACKAGECONFIG:append = " gdkpixbuf"

FILESEXTRAPATHS:append := "${THISDIR}/files:"

SRC_URI += "file://0001-add-has_argument-ref-meson-1.3.1-version.patch \
            "

