rescue_configure() {
    echo ${MACHINE}
    if [ "${MACHINE}" = "sl1620" ]; then
        MACHINE_NAME="myna2"
    elif [ "${MACHINE}" = "sl1640" ]; then
        MACHINE_NAME="platypus"
    elif [ "${MACHINE}" = "sl1680" ]; then
        MACHINE_NAME="dolphin"
    else
        echo "Unsupported MACHINE: ${MACHINE}"
        return 0
    fi

    UBOOT_DEFCONFIG="${S}/configs/${MACHINE_NAME}_suboot_defconfig"

    if [ -f "${UBOOT_DEFCONFIG}" ]; then
        sed -i '/^CONFIG_SYNA_RESCUE_MODE[ =]/d' "${UBOOT_DEFCONFIG}"

        if [ "${ENABLE_RESCUE_MODE}" = "1" ]; then
            echo "Appending CONFIG_SYNA_RESCUE_MODE=y to ${UBOOT_DEFCONFIG}"
            echo 'CONFIG_SYNA_RESCUE_MODE=y' >> "${UBOOT_DEFCONFIG}"

            sed -i '/^CONFIG_ENV_OFFSET=/d' "${UBOOT_DEFCONFIG}"
            echo "CONFIG_ENV_OFFSET=0x77f0000" >> "${UBOOT_DEFCONFIG}"

            sed -i '/^CONFIG_SYS_REDUNDAND_ENVIRONMENT/d' "${UBOOT_DEFCONFIG}"
            echo "#CONFIG_SYS_REDUNDAND_ENVIRONMENT is not set" >> "${UBOOT_DEFCONFIG}"

        else
            echo "ENABLE_RESCUE_MODE != 1, not enabling CONFIG_SYNA_RESCUE_MODE"

            sed -i '/^CONFIG_ENV_OFFSET=/d' "${UBOOT_DEFCONFIG}"
            echo "CONFIG_ENV_OFFSET=0x3ff0000" >> "${UBOOT_DEFCONFIG}"

            sed -i '/^# CONFIG_SYS_REDUNDAND_ENVIRONMENT is not set/d' "${UBOOT_DEFCONFIG}"
            grep -q '^CONFIG_SYS_REDUNDAND_ENVIRONMENT=' "${UBOOT_DEFCONFIG}" || \
                echo "CONFIG_SYS_REDUNDAND_ENVIRONMENT=y" >> "${UBOOT_DEFCONFIG}"
        fi
    else
        echo "WARNING: UBOOT_DEFCONFIG file not found: ${UBOOT_DEFCONFIG}"
    fi
}

do_configure:prepend:dolphin() {
    rescue_configure
}

do_configure:prepend:platypus() {
    rescue_configure
}

do_configure:prepend:myna2() {
    rescue_configure
}
