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
   file://mali_icd.json \
   file://VkLayer_window_system_integration.json \
"

SRCREV = "${SYNA_SRCREV_LINUX_SYSROOT}"

PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}"

do_install () {
}

do_install:append:aarch64 () {
    for i in ${S}/${PREBUILT_LIBS}/*; do
        if [ -d "${i}" ]; then
            dir_name=$(basename "${i}")
            install -d ${D}${libdir}/${dir_name}
            cp -r ${i}/* ${D}${libdir}/${dir_name}/
        else
            file_name=$(basename "${i}")
            install -Dm0755 ${i} ${D}${libdir}/${file_name}
        fi
    done

    # Create symlinks for Mali libraries
    cd ${D}${libdir}

    # Mali GL libraries
    if [ -f libmali.so.0.49.1 ]; then
        ln -sf libmali.so.0.49.1 libmali.so.0
        ln -sf libmali.so.0 libmali.so
    fi

    # Mali OpenCL libraries
    if [ -f libmali_opencl.so.0.49.1 ]; then
        ln -sf libmali_opencl.so.0.49.1 libmali_opencl.so.0
        ln -sf libmali_opencl.so.0 libmali_opencl.so
    fi

    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${WORKDIR}/gfx_files/*.pc ${D}${libdir}/pkgconfig/
    install -d ${D}${includedir}
    cp -r ${WORKDIR}/gfx_files/includes/* ${D}${includedir}/
}

do_install:append() {
    install -d ${D}${sysconfdir}/xdg/vulkan/icd.d
    install -m 0644 ${WORKDIR}/mali_icd.json ${D}${sysconfdir}/xdg/vulkan/icd.d/mali_icd.json
    install -d ${D}${sysconfdir}/xdg/vulkan/implicit_layer.d
    install -m 0644 ${WORKDIR}/VkLayer_window_system_integration.json ${D}${sysconfdir}/xdg/vulkan/implicit_layer.d/VkLayer_window_system_integration.json
}

FILES:${PN} += " \
    ${sysconfdir}/xdg/vulkan/icd.d/mali_icd.json \
    ${sysconfdir}/xdg/vulkan/implicit_layer.d/VkLayer_window_system_integration.json \
    ${libdir}/vulkan/implicit_layer.d/libVkLayer_window_system_integration.so \
    ${libdir}/vulkan/implicit_layer.d/VkLayer_window_system_integration.json \
"

SOLIBS = ".so*"
FILES_SOLIBSDEV = ""

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

INSANE_SKIP:${PN} += "dev-so file-rdeps already-stripped"

sysroot_stage_mali_linker_scripts() {
    for lib in EGL GLESv2 GLESv1_CM gbm; do
        link="${SYSROOT_DESTDIR}${libdir}/lib${lib}.so"
        if [ -f "${link}" ] || [ -L "${link}" ]; then
            rm -f "${link}"
            printf 'INPUT(libmali.so.0)\n' > "${link}"
        fi
    done
}
SYSROOT_PREPROCESS_FUNCS += "sysroot_stage_mali_linker_scripts"

