SUMMARY = "Single-header C subprocess helper (sheredom/subprocess.h)"
HOMEPAGE = "https://github.com/sheredom/subprocess.h"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7246f848faa4e9c9fc0ea91122d6e680"

SRC_URI = "git://github.com/sheredom/subprocess.h.git;branch=master;protocol=https"
SRCREV = "b49c56e9fe214488493021017bf3954b91c7c1f5"

PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"

inherit allarch

do_install() {
    install -d ${D}${includedir}
    install -m 0644 subprocess.h ${D}${includedir}/subprocess.h
}

RDEPENDS:${PN}-dev = ""

FILES:${PN}-dev = "${includedir}/subprocess.h"

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"

