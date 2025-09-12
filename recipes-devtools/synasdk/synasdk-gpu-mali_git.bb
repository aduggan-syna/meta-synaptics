DESCRIPTION = "Synaptics Mali GPU binary files"
SECTION = "libs"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r1"

PROVIDES = "virtual/egl virtual/libgles1 virtual/libgles2 virtual/libgles3 virtual/libgbm virtual/libgl"

RDEPENDS:${PN} = " \
    libffi \
    libdrm \
    wayland \
"

COMPATIBLE_MACHINE = "syna"
SYNAMACH:klamath = "sl2619"

PREBUILT_PATH = "sysroot/linux-baseline/data/gfx_prebuilt/mali/${SYNAMACH}"
DISPLAY_SERVER = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', 'gbm', d)}"
PREBUILT_LIBS = "sysroot/linux-baseline/data/gfx_prebuilt/mali/${DISPLAY_SERVER}/${SYNAMACH}/${HOST_SYS}/lib/"
PREBUILT_BINS = "sysroot/linux-baseline/data/gfx_prebuilt/mali/${DISPLAY_SERVER}/${SYNAMACH}/${HOST_SYS}/bin/"

SRC_URI = " \
   ${SYNA_SRC_LINUX_SYSROOT} \
   file://gfx_files/ \
"

SRCREV = "${SYNA_SRCREV_LINUX_SYSROOT}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}"

do_install () {
}

do_install:append:aarch64 () {
    for i in ${S}/${PREBUILT_LIBS}/*\.so*; do
        file_name=`basename "${i}"`
        install -Dm0644 ${i} ${D}${libdir}/${file_name}
    done

    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${WORKDIR}/gfx_files/*.pc ${D}${libdir}/pkgconfig/
    install -d ${D}${includedir}
    cp -r ${WORKDIR}/gfx_files/includes/* ${D}${includedir}/
}

SOLIBS = ".so*"
FILES_SOLIBSDEV = ""

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
