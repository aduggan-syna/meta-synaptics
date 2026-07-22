SUMMARY = "TORQ IREE host tools (native) - extracted from torq_runtime wheel v2.1.0b2"
LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "https://github.com/synaptics-torq/torq-compiler/releases/download/v2.1.0_beta2/torq_runtime-2.1.0b2-cp312-cp312-manylinux_2_28_x86_64.whl;name=runtime_wheel;subdir=wheel_contents;downloadfilename=torq_runtime-2.1.0b2-x86_64.zip"

SRC_URI[runtime_wheel.sha256sum] = "2e62449327c78d5d581659cb8c3f57fa4ae83182ae53ce49022f43ca32805eb0"

PV = "2.1.0b2"
S = "${WORKDIR}/wheel_contents"

inherit native

PROVIDES += "torq-hosttools-native"
PN = "torq-hosttools-native"

do_install() {
    install -d ${D}${bindir}/iree

    install -m 0755 ${S}/iree/_runtime_libs/iree-c-embed-data ${D}${bindir}/iree/
    install -m 0755 ${S}/iree/_runtime_libs/iree-flatcc-cli ${D}${bindir}/iree/
}

INSANE_SKIP:${PN} = "already-stripped ldflags"