do_configure:prepend() {
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

    UBOOT_DEFCONFIG="${S}/boot/u-boot_2019_10/configs/${MACHINE_NAME}_suboot_defconfig"

    if [ -f "${UBOOT_DEFCONFIG}" ]; then
	sed -i '/^CONFIG_SYNA_RESCUE_MODE[ =]/d' "${UBOOT_DEFCONFIG}"

	if [ "${ENABLE_RESCUE_MODE}" = "1" ]; then
            echo "Appending CONFIG_SYNA_RESCUE_MODE=y to ${UBOOT_DEFCONFIG}"
            echo 'CONFIG_SYNA_RESCUE_MODE=y' >> "${UBOOT_DEFCONFIG}"
	else
            echo "ENABLE_RESCUE_MODE != 1, not enabling CONFIG_SYNA_RESCUE_MODE"
	fi
    else
	echo "WARNING: UBOOT_DEFCONFIG file not found: ${UBOOT_DEFCONFIG}"
    fi
}
