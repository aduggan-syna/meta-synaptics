SUMMARY = "Arm GNU Toolchain - x86_64 GNU/Linux target (arm-none-eabi)"
LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"

COMPATIBLE_HOST = "x86_64.*-linux"

FILES:${PN} = "${libexecdir} ${bindir}"

inherit native

INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_DEFAULT_DEPS = "1"

LIC_FILES_CHKSUM = "file://${WORKDIR}/EULA;md5=6a034e43fab16794f2c743f6003936b6"

#REALPV = "${@d.getVar('PV').replace('rel1','Rel1')}"

SRC_URI = " \
    https://developer.arm.com/-/media/Files/downloads/gnu/${PV}/binrel/arm-gnu-toolchain-${PV}-x86_64-arm-none-eabi.tar.xz;name=gcc \
    https://developer.arm.com/GetEula?Id=37988a7c-c40e-4b78-9fd1-62c20b507aa8;downloadfilename=EULA;name=eula \
"

SRC_URI[gcc.md5sum] = "791754852f8c18ea04da7139f153a5b7"
SRC_URI[eula.md5sum] = "6a034e43fab16794f2c743f6003936b6"

S = "${WORKDIR}/arm-gnu-toolchain-${@d.getVar('PV').replace('rel1','Rel1')}-x86_64-arm-none-eabi"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${libexecdir}/${BP}

    # Copy everything (for reference/debug)
    cp -r ${S}/* ${D}${libexecdir}/${BP}/

    for f in ${D}${libexecdir}/${BP}/bin/*; do
       ln -rs $f ${D}${bindir}/$(basename $f)
    done
}
