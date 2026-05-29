FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://sl1620_emmc.pt \
            file://sl1640_emmc.pt \
            file://sl1680_emmc.pt \
            file://sl2619_emmc.pt"
do_install:append() {
    if [ "${ENABLE_RESCUE_MODE}" = "1" ]; then
        if [ "${MACHINE}" = "sl1620" ]; then
            install -m 0644 ${WORKDIR}/sl1620_emmc.pt ${D}${datadir}/syna/build/emmc.pt
        elif [ "${MACHINE}" = "sl1640" ]; then
            install -m 0644 ${WORKDIR}/sl1640_emmc.pt ${D}${datadir}/syna/build/emmc.pt
        elif [ "${MACHINE}" = "sl1680" ]; then
            install -m 0644 ${WORKDIR}/sl1680_emmc.pt ${D}${datadir}/syna/build/emmc.pt
        elif [ "${MACHINE}" = "sl2619" ]; then
            install -m 0644 ${WORKDIR}/sl2619_emmc.pt ${D}${datadir}/syna/build/emmc.pt
        else
            echo "Unsupported MACHINE: ${MACHINE}"
            return 0
        fi
    fi
}
