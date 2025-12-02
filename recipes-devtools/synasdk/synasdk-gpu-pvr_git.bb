DESCRIPTION = "Synaptics ImaginationTech PowerVR GPU binary files"
SECTION = "libs"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r1"

PROVIDES = " \
    virtual/libpvr-dri-support virtual/libgles1-pvr virtual/libgles2-pvr \
    virtual/libpvr-scope-srv \
    virtual/pvr-fw \
"

DISPLAY_SERVER_DEPS = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libx11', 'wayland', d)}"

RDEPENDS:${PN} = " \
    libffi \
    libdrm \
    ${DISPLAY_SERVER_DEPS} \
    imgtec-pvr-firmware \
    libglapi libgles1-mesa libegl-mesa libgbm mesa-vulkan-drivers \
    libgles2-mesa \
    vulkan-loader \
"

COMPATIBLE_MACHINE = "syna"
SYNAMACH:platypus = "sl1640"
SYNAMACH:dolphin = "sl1680"
SYNAMACH:myna2 = "sl1620"
# Fix me: use sl1620 gpu binary for klamath to pass build
SYNAMACH:klamath = "sl1620"

PREBUILT_PATH = "sysroot/linux-baseline/data/gfx_prebuilt/imagination/${SYNAMACH}"
DISPLAY_SERVER = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'Xorg', 'wayland', d)}"
PREBUILT_LIBS = "sysroot/linux-baseline/data/gfx_prebuilt/imagination/${DISPLAY_SERVER}/${SYNAMACH}/${HOST_SYS}/lib/"
PREBUILT_BINS = "sysroot/linux-baseline/data/gfx_prebuilt/imagination/${DISPLAY_SERVER}/${SYNAMACH}/${HOST_SYS}/bin/"

SRC_URI = " \
   ${SYNA_SRC_LINUX_SYSROOT} \
   file://powervr_icd.json \
"

SRCREV = "${SYNA_SRCREV_LINUX_SYSROOT}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}"

do_install () {
    # install header files
    for i in ${S}/${PREBUILT_PATH}/include/*\.h; do
        file_name=`basename "${i}"`
        install -Dm0644 ${i} ${D}${includedir}/powervr/${file_name}
    done

    # install PowerVR Firmware
    install -d ${D}${nonarch_base_libdir}/firmware
    for i in ${S}/${PREBUILT_PATH}/fw/*; do
        install -m 0644 ${i} ${D}${nonarch_base_libdir}/firmware
    done
}

do_install:append:aarch64 () {

    for i in ${S}/${PREBUILT_LIBS}/*\.so*; do
        file_name=`basename "${i}"`

        if [ "${file_name}" = "libvulkan.so" ] || [ "${file_name}" = "libvulkan.so.1" ]; then
            echo "Skipping ${file_name}"
            continue
        fi

        install -Dm0644 ${i} ${D}${libdir}/${file_name}
    done

    for i in ${S}/${PREBUILT_BINS}/*; do
        file_name=`basename "${i}"`
        install -Dm0755 ${i} ${D}${bindir}/${file_name}
    done

    # Check if ${D}${libdir}/libVK_IMG.so exists and create a symbolic link
    if [ -f "${D}${libdir}/libVK_IMG.so" ]; then \
        cd ${D}${libdir}
        ln -sf "libVK_IMG.so" "libVK_IMG.so.1"; \
        cd -
    fi

    # Install Vulkan ICD config
    install -d ${D}${datadir}/vulkan/icd.d
    install -m0644 ${WORKDIR}/powervr_icd.json ${D}${datadir}/vulkan/icd.d/powervr_icd.json

}

do_install:append:arm () {
    pushd ${S}/${PREBUILT_PATH}

    # install MESA backend
    for i in \
        libsrv_um.so libsutu_display.so libusc.so \
        libglslcompiler.so \
        libpvr_dri_support.so libGLESv1_CM_PVR_MESA.so libGLESv2_PVR_MESA.so \
        ;
    do
        [ -f lib/${i} ] || continue
        install -Dm0644 lib/${i} ${D}${libdir}/${i}
    done

    # PVRScope performance monitor
    for i in \
        libPVRScopeServices.so \
        ;
    do
        [ -f lib/${i} ] || continue
        install -Dm0644 lib/${i} ${D}${libdir}/${i}
    done

    popd
}

INSANE_SKIP:${PN} = "ldflags"
INSANE_SKIP:${MLPREFIX}imgtec-pvr-firmware = "arch"

SOLIBS = ".so*"
FILES_SOLIBSDEV = ""

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

PACKAGES:prepend = " \
    pvrscope \
    imgtec-pvr-firmware \
"

FILES:pvrscope = " \
    ${libdir}/libPVRScopeServices${SOLIBS} \
"

# The firmware is MIPS, not ARM!
FILES:imgtec-pvr-firmware = " \
    ${nonarch_base_libdir}/firmware/* \
"

FILES:${PN} += " \
    ${datadir}/vulkan/icd.d/powervr_icd.json \
"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
