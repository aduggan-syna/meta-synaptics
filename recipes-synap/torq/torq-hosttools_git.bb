SUMMARY = "IREE host tools (native) - prebuilt from release tarball"
LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"


SRC_URI = "https://github.com/synaptics-torq/torq-compiler/releases/download/snapshot/release.tar.gz;name=release"
SRC_URI[release.sha256sum] = "d1d052c7ff52f4682b7d86d9c2d645b8fadc8315eaeb099fdc0cfdb4b64f9e2b"
PV = "2.0.0_beta+snapshot"
S = "${WORKDIR}"

inherit native

PROVIDES += "torq-hosttools-native"
PN = "torq-hosttools-native"

do_install() {
    install -d ${D}${bindir}/iree
    install -m 0755 ${S}/release/tools/iree-c-embed-data ${D}${bindir}/iree/
    install -m 0755 ${S}/release/tools/iree-flatcc-cli ${D}${bindir}/iree/
}

INSANE_SKIP:${PN} = "already-stripped"

