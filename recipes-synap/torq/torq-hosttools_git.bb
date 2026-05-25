SUMMARY = "TORQ IREE host tools (native) - prebuilt from v2.0.0_beta"
LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "https://github.com/synaptics-torq/torq-compiler/releases/download/v2.0.0_beta/release.tar.gz;name=release"

SRC_URI[release.sha256sum] = "d6369bdba7e097ee44c75872308d7e8f73273e33e40f0140a61c22e44eafde43"

PV = "2.0.0_beta"
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