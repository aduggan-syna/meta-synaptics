FILESEXTRAPATHS:prepend := "${THISDIR}/optee-os:"
require recipes-devtools/synasdk/synasdk-config.inc

PATCHTOOL = "git"

SRC_URI += " \
    file://astra-optee-os.patch \
"
OPTEEMACHINE = "synaptics"
SYNA_CHIP:myna2 = "myna2"
SYNA_CHIP:dolphin = "dolphin"
SYNA_CHIP:platypus = "platypus"
SYNA_CHIP:klamath = "klamath"

COMPATIBLE_MACHINE:myna2 = "sl1620"
COMPATIBLE_MACHINE:platypus = "sl1640"
COMPATIBLE_MACHINE:dolphin = "sl1680"
COMPATIBLE_MACHINE:klamath = "sl2619"

EXTRA_OEMAKE += " PLATFORM_FLAVOR=${SYNA_CHIP} "

SRC_URI:append: = "${SYNA_SRC_TEE} "
SRCREV_tee = "${SYNA_SRCREV_TEE}"
SRCREV_FORMAT = "tee"

SYNA_TEE_PATH = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee"
do_compile:prepend() {
    . ${CONFIG_FILE}
    . ${CHIP_RC_FILE}

    if [ "is${syna_chip_name}" != "isdolphin" ];then
        mr_file="${SYNA_TEE_PATH}/tee/products/${syna_chip_name}/${CONFIG_TZK_MEM_LAYOUT}/mr_config"
    else
        mr_file="${SYNA_TEE_PATH}/tee/products/${syna_chip_name}/genx/${CONFIG_TZK_MEM_LAYOUT}/mr_config"
    fi
    bbnote "MR file: ${mr_file}"

    if [ ! -r "${mr_file}" ]; then
        bbfatal "Missing or unreadable MR file: ${mr_file}"
    fi

    spd_base="$(awk '/\<Secure\>/{print $2}' ${mr_file})"
    spd_size="$(awk '/\<Secure\>/{print $3}' ${mr_file})"

    if [ -z "${spd_base}" ] || [ -z "${spd_size}" ]; then
        export CFG_SECURE_DATA_PATH="n"
    else
        export CFG_SECURE_DATA_PATH="y"
        export CFG_TEE_SDP_MEM_BASE="${spd_base}"
        export CFG_TEE_SDP_MEM_SIZE="${spd_size}"
    fi
}
