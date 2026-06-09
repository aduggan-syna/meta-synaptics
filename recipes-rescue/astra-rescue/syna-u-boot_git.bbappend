rescue_configure() {
    if [ "${MACHINE}" != "sl1620" ] && [ "${MACHINE}" != "sl1640" ] && [ "${MACHINE}" != "sl1680" ] && [ "${MACHINE}" != "sl2619" ] && [ "${MACHINE}" != "sl2619nand" ]; then
        echo "Unsupported MACHINE: ${MACHINE}"
        return 0
    fi

    UBOOT_DEFCONFIG="${S}/configs/${UBOOT_MACHINE}"

    if [ -f "${UBOOT_DEFCONFIG}" ]; then
        sed -i '/^CONFIG_SYNA_RESCUE_MODE[ =]/d' "${UBOOT_DEFCONFIG}"

        if [ "${ENABLE_RESCUE_MODE}" = "1" ]; then
            echo "Appending CONFIG_SYNA_RESCUE_MODE=y to ${UBOOT_DEFCONFIG}"
            echo 'CONFIG_SYNA_RESCUE_MODE=y' >> "${UBOOT_DEFCONFIG}"

            sed -i '/^CONFIG_ENV_OFFSET=/d' "${UBOOT_DEFCONFIG}"
            echo "CONFIG_ENV_OFFSET=0x9BF0000" >> "${UBOOT_DEFCONFIG}"

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

do_configure:prepend:klamath() {
    rescue_configure
}
