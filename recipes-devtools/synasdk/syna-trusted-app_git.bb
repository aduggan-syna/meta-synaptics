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
    # 1316a183 is the TA UUID common prefix.
    install -d ${D}${nonarch_base_libdir}/optee_armtz
    find "${S}/ta_enc" -type f \
        -regex ".*${syna_chip_name}/${syna_chip_rev}.*1316a183.*\.ta$" -exec sh -c \
        'install -Dm0644 {} ${D}${nonarch_base_libdir}/optee_armtz/$(basename {})' \;
}

FILES:${PN} = " \
    ${nonarch_base_libdir}/ta \
    ${nonarch_base_libdir}/firmware/ta \
    ${nonarch_base_libdir}/optee_armtz \
    ${libdir}/tee-supplicant/plugins \
"

do_deploy () {

    security_tools_path="${STAGING_DIR_NATIVE}${prefix}/libexec/syna/"
    security_keys_path="${STAGING_DATADIR_NATIVE}/syna/keys/${syna_chip_name}/${syna_chip_rev}"

    input_ta_path="${S}/ta_enc"
    input_sub_path="${syna_chip_name}/${syna_chip_rev}"

    # Use genimg to pack all preload TAs
    params=""

    if [ "is${syna_chip_name}" = "isdolphin" -a "is${CONFIG_GENX_ENABLE}" == "isy" ]; then
	    input_sub_path="${syna_chip_name}/${syna_chip_rev}/genx"
    fi

    if [ "is${CONFIG_BL_TA_FASTLOGO}" = "isy" ]; then
	    if [ -f ${input_ta_path}/libfastlogo.ta/${input_sub_path}/1316a183-894d-43fe-9893-bb946ae103f5.ta ]; then
		    params="$params -i 03F5 -d ${input_ta_path}/libfastlogo.ta/${input_sub_path}/1316a183-894d-43fe-9893-bb946ae103f5.ta"
	    else
		    echo "no 1316a183-894d-43fe-9893-bb946ae103f5.ta under ${input_ta_path}/libfastlogo.ta/${input_sub_path}!!!"
			    exit 1
	    fi
    fi

    genimg -n preload_ta -A 4096 $params -o ${DEPLOYDIR}/preload_ta.subimg
    rm ${DEPLOYDIR}/*.header
}

addtask deploy before do_package after do_install
