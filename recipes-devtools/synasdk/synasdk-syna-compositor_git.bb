SUMMARY = "Video Mixing with EGL and GLES2"
DESCRIPTION = "A library to mix DMA-BUF contents and render to another DMA-BUF using EGL and GLES2."
SECTION = "multimedia"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

DEPENDS = "virtual/egl virtual/libgles2 libdrm mesa"

SRC_URI = "${SYNA_SRC_APPLICATION}"
S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/application/syna-compositor"
SRCREV = "${SYNA_SRCREV_APPLICATION}"
PV = "${ASTRA_VERSION}+git${SRCPV}"
rootdir = "/home/root"
SRC_URI += " \
            file://syna-compositor.pc.in \
            "

COMPATIBLE_MACHINE = "syna"

inherit meson pkgconfig

EXTRA_OEMESON = ""

do_install() {
    install -d ${D}${libdir}
    install -m 0755 ${WORKDIR}/build/libsynacompositor.so ${D}${libdir}

    cd ${D}${libdir}
    ln -s libsynacompositor.so libsynacompositor.so.1

    install -d ${D}${includedir}
    cp -R --no-dereference --preserve=mode,links -v ${S}/include/* ${D}${includedir}/

    install -d ${D}${libdir}/pkgconfig

    sed 's/@@PV@@/${PV}/g' ${WORKDIR}/syna-compositor.pc.in > ${WORKDIR}/syna-compositor.pc

    install -m 0644 ${WORKDIR}/syna-compositor.pc ${D}${libdir}/pkgconfig/syna-compositor.pc

}

PACKAGES = " \
    synasdk-syna-compositor \
    synasdk-syna-compositor-dev \
    synasdk-syna-compositor-dbg \
"

FILES:${PN} = " \
    ${bindir}/* \
    ${libdir}/*.so* \
    ${includedir}/* \
    ${libdir}/pkgconfig/*.pc* \
    ${rootdir}/.profile \
"

FILES:${PN}-dev = " \
    ${includedir}/syna/* \
"

FILES:{$PN}-dbg = " \
    ${bindir}/.debug \
    ${libdir}/.debug \
"
INSANE_SKIP:${PN} += "ldflags"

