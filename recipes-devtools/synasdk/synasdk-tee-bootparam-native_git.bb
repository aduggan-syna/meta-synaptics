DESCRIPTION = "Synaptics TEE boot parameter generation"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"
PR = "r1"

SRC_URI = " \
    ${SYNA_SRC_TEE} \
"

SRCREV_tee = "${SYNA_SRCREV_TEE}"

SRCREV_FORMAT = "tee"

PV = "${ASTRA_VERSION}+git${SRCPV}"

DEPENDS += " synasdk-config-native cmake-native synasdk-tools-native synasdk-security-native"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee/tee"
B = "${WORKDIR}/${BP}"

require synasdk-build.inc

inherit allarch native

do_compile_bp () {
    if [ "is${syna_chip_name}" = "isdolphin" ]; then
        if [ "is${CONFIG_GENX_ENABLE}" = "isy" ]; then
            PLATFORM=${syna_chip_name}/genx/${CONFIG_TZK_MEM_LAYOUT}
        else
            PLATFORM=${syna_chip_name}/gen2/${CONFIG_TZK_MEM_LAYOUT}
        fi
    else
        PLATFORM=${syna_chip_name}/${CONFIG_TZK_MEM_LAYOUT}
    fi

    cmake -DPLATFORM=${PLATFORM} -DBERLIN_CHIP=${syna_chip_name} -S ${S}/tee/tee/tools/gen_boot_param
}

do_compile:append () {
    . ${CONFIG_FILE}
    . ${CHIP_RC_FILE}

    echo "CONFIG_TZK_MEM_LAYOUT = ${CONFIG_TZK_MEM_LAYOUT}"
    do_compile_bp

    make
        ${S}/gen_boot_param ${S}/bootparam.bin
        ${S}/gen_boot_param_recovery ${S}/bootparam_recovery.bin

    . ${S}/build/module/tee/script/common.rc

    security_tools_path="${STAGING_DIR_NATIVE}/usr/libexec/syna/"
    export PATH=$PATH:$security_tools_path

    security_keys_path="${STAGING_DATADIR_NATIVE}/syna/keys/${syna_chip_name}/${syna_chip_rev}"

    CONFIG_RDK_SYS=y

    if [ "is${syna_chip_name}" = "isdolphin" ]; then
        if [ "is${CONFIG_GENX_ENABLE}" = "isy" ]; then
            dst_tee_dir=${B}/tee/${syna_chip_name}/genx/${tz_memlayout}/${syna_chip_rev}
            dst_oem_dir=${B}/tee/${syna_chip_name}/genx/${tz_memlayout}
        else
            dst_tee_dir=${B}/tee/${syna_chip_name}/gen2/${tz_memlayout}/${syna_chip_rev}
            dst_oem_dir=${B}/tee/${syna_chip_name}/gen2/${tz_memlayout}
        fi
    else
        dst_tee_dir=${B}/tee/${syna_chip_name}/${tz_memlayout}/${syna_chip_rev}
        dst_oem_dir=${B}/tee/${syna_chip_name}/${tz_memlayout}/
    fi

    mkdir -pv ${dst_tee_dir}

    if [ "is${CONFIG_GENX_ENABLE}" = "isy" ]; then
        # these are required by parse_addr.rc
        tz_memlayout=${CONFIG_TZK_MEM_LAYOUT}

        if [ "is${syna_chip_name}" = "isdolphin" ]; then
            tz_rel_ver=genx
        fi

        if [ "is${CONFIG_GENX_MCU}" = "isy" ]; then
            tool_version=genx_v3
        else
            tool_version=genx
        fi

        module_topdir="${S}/tee/tee"
        echo "$module_topdir = ${module_topdir}"

        . ${S}/build/module/tee/script/parse_addr.rc

        ${security_tools_path}in_extras.py "TZK_BOOT_PARAMETER" ${S}/boot_param_extras.bin 0x00000000 ${tzbp_addr}

        in_bin=${S}/bootparam.bin
        out_bin=${dst_tee_dir}/bootparam_en.bin
        # Generate image
        gen_x_secure_image --chip-name=${syna_chip_name} \
                       --chip-rev=${syna_chip_rev} \
                       --img_type="TZK_BOOT_PARAMETER_OPEN" \
                       --key_type="ree" \
                       --length=0x0 --extras=${S}/boot_param_extras.bin \
                       --workdir-security-tools=${security_tools_path} \
                       --workdir-security-keys=${security_keys_path} \
                       --tool-version=${tool_version} \
                       --in_payload=${in_bin} \
                       --out_store=${out_bin}

        in_bin=${S}/bootparam_recovery.bin
        out_bin=${dst_tee_dir}/bootparam_recovery_en.bin
        gen_x_secure_image --chip-name=${syna_chip_name} \
                       --chip-rev=${syna_chip_rev} \
                       --img_type="TZK_BOOT_PARAMETER_OPEN" \
                       --key_type="ree" \
                       --length=0x0 --extras=${S}/boot_param_extras.bin \
                       --workdir-security-tools=${security_tools_path} \
                       --workdir-security-keys=${security_keys_path} \
                       --tool-version=${tool_version} \
                       --in_payload=${in_bin} \
                       --out_store=${out_bin}
    fi
}

do_install:append () {
    # bootparam
    install -d ${D}/${datadir}/syna/tee/bootparam/
    cp -av ${B}/tee/* ${D}/${datadir}/syna/tee/bootparam
    cp -av ${S}/bootparam.bin.mr ${D}/${datadir}/syna/tee/bootparam/mr_config
}

FILES:${PN} = "\
    ${datadir}/syna/tee/bootparam \
"
