SUMMARY = "Arm GNU Toolchain - AArch32 target with hard float (arm-linux-gnueabihf)"
LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"

COMPATIBLE_HOST = "x86_64.*-linux"

FILES:${PN} = "${libexecdir} ${bindir}"

inherit native

INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_DEFAULT_DEPS = "1"

LIC_FILES_CHKSUM = "file://${WORKDIR}/EULA;md5=6a034e43fab16794f2c743f6003936b6"

SRC_URI = " \
    https://developer.arm.com/-/media/Files/downloads/gnu-a/${PV}/binrel/gcc-arm-${PV}-x86_64-arm-linux-gnueabihf.tar.xz;name=gcc \
    https://github.com/synaptics-astra/sdk/releases/download/scarthgap_6.12_v2.4.0/EULA;downloadfilename=EULA;name=eula
"

SRC_URI[gcc.md5sum] = "650dc30f7e937fa12e37ea70ff6e10dd"
SRC_URI[eula.md5sum] = "6a034e43fab16794f2c743f6003936b6"

S = "${WORKDIR}/gcc-arm-${PV}-x86_64-arm-linux-gnueabihf"

do_install() {
    install -d ${D}${bindir} ${D}${libexecdir}/${BP}/
    cp -r ${S}/. ${D}${libexecdir}/${BP}

    # Symlink all executables into bindir
    for f in ${D}${libexecdir}/${BP}/bin/*; do
        ln -rs $f ${D}${bindir}/$(basename $f)
    done
}
