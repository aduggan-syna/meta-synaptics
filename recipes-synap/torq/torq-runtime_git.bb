SUMMARY = "TORQ runtime for target"
LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2e982d844baa4df1c80de75470e0c5cb"

SRC_URI = " \
    git://github.com/synaptics-torq/torq-compiler.git;branch=main;protocol=https;submodules=1;name=torq \
    git://github.com/synaptics-torq/iree.git;branch=torq-old;protocol=https;name=iree;submodules=1;destsuffix=git/third_party/iree \
    git://github.com/google/benchmark.git;branch=main;protocol=https;name=benchmark;submodules=1;destsuffix=git/third_party/iree/third_party/benchmark \
    git://github.com/google/googletest.git;branch=main;protocol=https;name=googletest;submodules=1;destsuffix=git/third_party/iree/third_party/googletest \
    git://github.com/dvidelabs/flatcc.git;branch=master;protocol=https;name=flatcc;submodules=1;destsuffix=git/third_party/iree/third_party/flatcc \
"
SRCREV_torq = "8c4477000abc0ac19d5c9b123bd43c6ec8deef8d"
SRCREV_iree = "0802453ba39cc43bae75f05610e34cd35a4892e9"
SRCREV_benchmark = "1e96bb0ab5e758861f5bbbd4edbd0a8d9a2a7cae"
SRCREV_googletest = "c8393f8554419dc27b688c535b8fa4afb82146a4"
SRCREV_flatcc = "9362cd00f0007d8cbee7bff86e90fb4b6b227ff3"

SRCREV_FORMAT = "torq"
PV = "git+${@d.getVar('SRCREV_torq')[:8]}"
S = "${WORKDIR}/git"

inherit cmake pkgconfig
OECMAKE_GENERATOR = "Unix Makefiles"

DEPENDS += "torq-hosttools-native"
EXTRA_OECMAKE += "\
  -DCMAKE_BUILD_TYPE=Release \
  -DIREE_BUILD_COMPILER=OFF \
  -DTORQ_ENABLE_AWS_FPGA=OFF \
  -DTORQ_ENABLE_SOC_FPGA=OFF \
  -DTORQ_ENABLE_ASTRA_MACHINA=ON \
  -DTORQ_ENABLE_SIMULATOR=OFF \
  -DIREE_HAL_DRIVER_LOCAL_SYNC=ON \
  -DIREE_HAL_DRIVER_LOCAL_TASK=ON \
  -DIREE_BUILD_TOOLS=OFF \
  -DIREE_ENABLE_CPUINFO=OFF \
  -DCPUINFO_BUILD_BENCHMARKS=OFF \
  -DCPUINFO_BUILD_TOOLS=OFF \
  -DBENCHMARK_ENABLE_GTEST_TESTS=OFF \
  -DIREE_HOST_BIN_DIR=${STAGING_BINDIR_NATIVE}/iree \
"
OECMAKE_TARGET_COMPILE = "iree-run-module iree_runtime_unified"

IREE_BUILD_DIR ?= "${B}"
DEP_LIBS = " \
    third_party/iree/runtime/plugins/TORQ/driver/libdriver_torq.a \
    third_party/iree/runtime/src/iree/hal/utils/libiree_hal_utils_deferred_command_buffer.a \
    third_party/iree/runtime/src/iree/hal/local/libiree_hal_local_local.a \
    third_party/iree/runtime/src/iree/hal/local/libiree_hal_local_executable_loader.a \
    third_party/iree/runtime/src/iree/base/internal/libiree_base_internal_arena.a \
    third_party/iree/runtime/src/iree/hal/utils/libiree_hal_utils_resource_set.a \
    third_party/iree/runtime/plugins/TORQ/torq_hw/libtorq_hw_TorqHw.a \
    third_party/iree/runtime/src/iree/base/internal/libiree_base_internal_fpu_state.a \
    third_party/iree/runtime/src/iree/hal/utils/libiree_hal_utils_semaphore_base.a \
    third_party/iree/runtime/src/iree/hal/local/libiree_hal_local_executable_environment.a \
    third_party/iree/runtime/src/iree/hal/utils/libiree_hal_utils_memory_file.a \
    third_party/iree/runtime/src/iree/io/libiree_io_stdio_stream.a \
    third_party/iree/runtime/src/iree/base/internal/libiree_base_internal_atomic_slist.a \
    third_party/iree/runtime/src/iree/base/internal/libiree_base_internal_cpu.a \
    third_party/iree/runtime/src/iree/hal/libiree_hal_hal.a \
    third_party/iree/runtime/src/iree/tooling/libiree_tooling_numpy_io.a \
    third_party/iree/runtime/src/iree/hal/utils/libiree_hal_utils_file_transfer.a \
    third_party/iree/runtime/src/iree/base/internal/libiree_base_internal_flags.a \
    third_party/iree/runtime/src/iree/base/internal/libiree_base_internal_file_io.a \
    third_party/iree/runtime/plugins/TORQ/driver/registration/libdriver_registration_registration.a \
    third_party/iree/runtime/src/iree/hal/local/plugins/registration/libiree_hal_local_plugins_registration_registration.a \
    third_party/iree/runtime/src/iree/hal/local/libiree_hal_local_executable_plugin_manager.a \
    third_party/iree/runtime/src/iree/hal/local/loaders/registration/libiree_hal_local_loaders_registration_registration.a \
    third_party/iree/build_tools/third_party/flatcc/libflatcc_parsing.a \
"

COMBINED_OUTPUT ?= "${B}/libdriver_torq_full.a"
do_combine_staticlib() {
    set -e
    tmp="${T}/combine_libs"
    rm -rf "${tmp}"; mkdir -p "${tmp}"

    for lib in ${DEP_LIBS}; do
        src="${B}/${lib}"
        [ -f "${src}" ] || bbfatal "Missing archive: ${src}"
        sub="${tmp}/${lib%.a}"
        mkdir -p "${sub}"
        ( cd "${sub}" && ${AR} x "${src}" )
        # Prefix object names to avoid filename clashes
        for o in "${sub}"/*.o; do
            [ -f "${o}" ] && mv "${o}" "${sub}/$(basename ${lib%.a})_$(basename "${o}")"
        done
    done

    rm -f "${COMBINED_OUTPUT}"
    ${AR} rcs "${COMBINED_OUTPUT}" $(find "${tmp}" -type f -name '*.o')
    ${RANLIB} "${COMBINED_OUTPUT}" || true

    echo "Created: ${COMBINED_OUTPUT}"
}
addtask combine_staticlib after do_compile before do_install

do_install() {
    # Install binary
    install -d ${D}${bindir}
    install -m 0755 ${B}/third_party/iree/tools/iree-run-module ${D}${bindir}/

    # Install static library
    install -d ${D}${libdir}
    install -m 0644 ${B}/third_party/iree/runtime/src/iree/runtime/libiree_runtime_unified.a ${D}${libdir}/

    # Install combined output
    if [ -n "${COMBINED_OUTPUT}" ] && [ -f "${COMBINED_OUTPUT}" ]; then
        install -m 0644 "${COMBINED_OUTPUT}" "${D}${libdir}/"
    fi
}

# Install include files and create pkgconfig
IREE_SRC_ROOT = "${S}/third_party/iree/runtime/src"
do_install:append() {
    install -d ${D}${includedir}
    # The 'cp --parents' flag recreates the directory tree relative to ${IREE_SRC_ROOT}
    (
        cd ${IREE_SRC_ROOT}
        find . -type f -name '*.h' -exec install -D -m 0644 "{}" "${D}${includedir}/{}" \;
    )
    install -d ${D}${libdir}/pkgconfig
    cat > ${D}${libdir}/pkgconfig/torq-runtime.pc << 'EOF'
prefix=${prefix}
exec_prefix=${exec_prefix}
libdir=${libdir}
includedir=${includedir}

Name: torq-runtime
Description: TORQ runtime + IREE runtime headers
Version: ${PV}
Libs: -L${libdir} -ldriver_torq_full -liree_runtime_unified.a
Cflags: -I${includedir}/iree
EOF
}    


FILES:${PN} += "${bindir}/iree-run-module"
FILES:${PN}-staticdev += "${libdir}/libiree_runtime_unified.a"
FILES:${PN}-staticdev += "${libdir}/$(basename ${COMBINED_OUTPUT})"
FILES:${PN}-dev += " \
    ${includedir}/iree \
    ${libdir}/pkgconfig/torq-runtime.pc \
"
COMPATIBLE_MACHINE = "syna"
SYNAMACH:klamath = "sl2619"
