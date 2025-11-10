DESCRIPTION = "SyNAP Runtime"
SECTION = "devtools"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = " \
    git://github.com/synaptics-synap/runtime.git;branch=main;protocol=https;submodules=1;name=synapruntime \
"
SRC_URI += "file://synap-runtime.pc.in"

SRCREV_synapruntime = "5f7961cfb27a1e09ae77922753793840155264bb"
SRCREV_FORMAT = "synapruntime"

PV = "${SYNAP_VERSION}+git+${@d.getVar('SRCREV_synapruntime')[:8]}"
S = "${WORKDIR}/git"

# synap libraries are not versioned so we need to make sure they end up in the
# ${PN} package and not ${PN}-dev package
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

inherit cmake

do_install:append () {
    install -d ${D}${libdir}/pkgconfig
    sed 's/@@PV@@/${PV}/g' ${WORKDIR}/synap-runtime.pc.in > ${WORKDIR}/synap-runtime.pc
    install -m 0644 ${WORKDIR}/synap-runtime.pc ${D}${libdir}/pkgconfig/synap-runtime.pc
}

DEPENDS:klamath = "torq-runtime"
DEPENDS:append:aarch64 = " tensorflow-lite"

EXTRA_OECMAKE = "\
  -DVSSDK_DIR=${WORKDIR}/${SYNA_SOURCE_PREFIX} \
  -DCMAKE_BUILD_TYPE=Release \
  -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
  -DSYNAP_SYSROOT_INCLUDEDIR=${RECIPE_SYSROOT}${includedir} \
  -DSYNAP_SYSROOT_LIBDIR=${RECIPE_SYSROOT}${libdir} \
"

EXTRA_OECMAKE:append:aarch64 = " -DENABLE_TFLITERUNTIME=ON"
EXTRA_OECMAKE:append:klamath = " -DENABLE_EBGRUNTIME=OFF -DENABLE_TORQRUNTIME=ON"

INSANE_SKIP:${PN} += "already-stripped"
PACKAGES = "${PN} ${PN}-dbg ${PN}-dev ${PN}-staticdev"

COMPATIBLE_MACHINE = "syna"
SYNAMACH:klamath = "sl2619"
