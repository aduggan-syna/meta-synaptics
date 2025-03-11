DESCRIPTION = "Synaptics Trusted Application"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r2"

inherit deploy

DEPENDS += " synasdk-tools-native"

COMPATIBLE_MACHINE = "syna"
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = " \
    ${SYNA_SRC_TA_ENC} \
"

SRCREV_taenc = "${SYNA_SRCREV_TA_ENC}"

SRCREV_FORMAT = "taenc"

require synasdk-build.inc

PV = "${ASTRA_VERSION}+git${SRCPV}"
INSANE_SKIP:${PN} += "file-rdeps"

do_compile () {
    # We may not need them from the syna-release package
    security_tools_path="${STAGING_DIR_NATIVE}${prefix}/libexec/syna/"
    security_keys_path="${STAGING_DATADIR_NATIVE}/syna/keys/${syna_chip_name}/${syna_chip_rev}"

    . ${S}/build/install.rc
    . ${S}/build/module/ta_enc/ta_list.rc
    . ${S}/build/module/ta_enc/common.rc
}

do_install () {
    install -d ${D}${libdir}/tee-supplicant/plugins
    find "${S}/ta_enc" -type f \
        -regex ".*${syna_chip_name}/${syna_chip_rev}.*1316a183.*\.plugin$" -exec sh -c \
        'install -Dm0644 {} ${D}${libdir}/tee-supplicant/plugins/$(basename {})' \;
}

FILES:${PN} = " \
    ${nonarch_base_libdir}/ta \
    ${nonarch_base_libdir}/firmware/ta \
    ${nonarch_base_libdir}/optee_armtz \
    ${libdir}/tee-supplicant/plugins \
"

do_deploy () {
}

addtask deploy before do_package after do_install
