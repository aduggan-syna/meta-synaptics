DESCRIPTION = "Synaptics genx_img Python implementation - built with PyInstaller"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r1"

inherit native

require synasdk-build.inc

SRCREV_FORMAT = "build"

PV = "${ASTRA_VERSION}+git${SRCPV}"

GENX_IMG_PY_STAGING_DIR = "${prefix}/libexec/syna-build"

# Dependencies - use existing Yocto recipes for PyInstaller and pycryptodome
DEPENDS += " \
    python3-native \
    python3-pyinstaller-native \
    python3-pycryptodome-native \
"

# Source directory for genx_img_py Python code
GENX_IMG_PY_SRCDIR = "${S}/build/tools/lib/sec_tools/bin/genx_img_py_src"

do_compile() {
    # Build directory for output
    local build_out="${B}/genx_img_py_out"
    mkdir -p "${build_out}"

    bbdebug 1 "Building genx_img_py from ${GENX_IMG_PY_SRCDIR}"

    if [ ! -d "${GENX_IMG_PY_SRCDIR}" ]; then
        bbfatal "genx_img_py source directory not found: ${GENX_IMG_PY_SRCDIR}"
    fi

    if [ ! -f "${GENX_IMG_PY_SRCDIR}/main.py" ]; then
        bbfatal "main.py not found in ${GENX_IMG_PY_SRCDIR}"
    fi

    cd "${GENX_IMG_PY_SRCDIR}"

    # Use PyInstaller from native sysroot (already built via DEPENDS)
    # PYTHONPATH includes pycryptodome and other dependencies from staging area
    export PYTHONPATH="${STAGING_DIR_NATIVE}${PYTHON_SITEPACKAGES_DIR}:${PYTHONPATH}"

    # Build with PyInstaller (--onefile creates a single executable). --clean
    # discards cached spec/build state so rebuilds are deterministic.
    ${STAGING_BINDIR_NATIVE}/pyinstaller --onefile --clean --noconfirm \
        --workpath "${build_out}/work" \
        --specpath "${build_out}" \
        --name genx_img_py \
        --distpath "${build_out}" \
        main.py

    pyinstaller_exit=$?

    if [ ${pyinstaller_exit} -ne 0 ]; then
        bbfatal "PyInstaller build failed for genx_img_py (exit code: ${pyinstaller_exit})"
    fi

    # Verify the output exists and is executable
    if [ ! -x "${build_out}/genx_img_py" ]; then
        bbfatal "genx_img_py executable not created or not executable: ${build_out}/genx_img_py"
    fi

    bbdebug 1 "Successfully built genx_img_py: ${build_out}/genx_img_py"
}

do_install() {
    local genx_img_py_bin="${B}/genx_img_py_out/genx_img_py"

    if [ ! -x "${genx_img_py_bin}" ]; then
        bbfatal "genx_img_py executable not found: ${genx_img_py_bin}"
    fi

    install -d ${D}${GENX_IMG_PY_STAGING_DIR}
    install -m 0755 "${genx_img_py_bin}" ${D}${GENX_IMG_PY_STAGING_DIR}/genx_img_py

    bbdebug 1 "Installed genx_img_py helper artifact to ${D}${GENX_IMG_PY_STAGING_DIR}/genx_img_py"
}

PACKAGES = "${PN}"

FILES:${PN} = " \
    ${GENX_IMG_PY_STAGING_DIR}/genx_img_py \
"
