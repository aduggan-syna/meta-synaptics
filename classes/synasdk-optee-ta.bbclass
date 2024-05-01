DEPENDS = "optee-client optee-os-tadevkit python3-cryptography-native"

inherit python3native

B = "${WORKDIR}/build"

require recipes-security/optee/optee.inc

EXTRA_OEMAKE += "TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
                 O=${B} \
                 CROSS_COMPILE=${HOST_PREFIX} \
               "

do_compile() {
    oe_runmake -f Makefile.op -C ${S}
}

do_compile[cleandirs] = "${B}"

do_install () {
    mkdir -p ${D}${nonarch_base_libdir}/optee_armtz
    install -D -p -m0444 ${B}/*.ta ${D}${nonarch_base_libdir}/optee_armtz
    install -D -p -m0444 ${B}/*.elf ${D}${nonarch_base_libdir}/optee_armtz
}

FILES:${PN} += "${nonarch_base_libdir}/optee_armtz/"

# Imports machine specific configs from staging to build
PACKAGE_ARCH = "${MACHINE_ARCH}"
