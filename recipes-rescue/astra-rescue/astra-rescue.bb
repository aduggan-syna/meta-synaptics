DESCRIPTION = "Install kernel, dtb, and initramfs into /rescue in target rootfs"
LICENSE = "CLOSED"

python __anonymous() {
    if d.getVar("ENABLE_RESCUE_MODE") != "1":
        raise bb.parse.SkipRecipe("astra-rescue is disabled (ENABLE_RESCUE_MODE != 1)")
}

inherit allarch systemd

DEPENDS += "virtual/kernel swupdate-image linux-syna core-image-initramfs-boot"

SRC_URI += "file://astra-rescue.service \
	    file://rescue-handler"

# Ensure it produces a package
PACKAGES = "${PN}"

do_compile[noexec] = "1"
do_install[depends] += "swupdate-image:do_image_complete linux-syna:do_deploy"

do_install() {
    install -d ${D}/rescue

    if [ "${SYNA_KERNEL_RESCUE_DTS_FILE}" != "" ]; then
        dtb_name=${SYNA_KERNEL_RESCUE_DTS_FILE}
    else
        dtb_name=${SYNA_KERNEL_DTS_FILE}
    fi
    dtb_name=${dtb_name##*/}
    dtb_name=${dtb_name%%.*}
    echo "rescue dtb_name = $dtb_name"

    KERNEL_FILE="${DEPLOY_DIR_IMAGE}/Image-${MACHINE}.bin"
    DTB_FILE=${DEPLOY_DIR_IMAGE}/${dtb_name}.dtb
    INITRAMFS_FILE=$(find ${DEPLOY_DIR_IMAGE} -name "swupdate-image-${MACHINE}.rootfs-*.cpio.gz" | head -n 1)

    # Compress and install files
    gzip -c -1 "$KERNEL_FILE" > ${D}/rescue/Image-${MACHINE}.gz
    install -m 0644 "$DTB_FILE" ${D}/rescue/rescue-${MACHINE}.dtb
    install -m 0644 "$INITRAMFS_FILE" ${D}/rescue/rescue-${MACHINE}.rootfs.cpio.gz

    # Install the script into /etc
    install -d ${D}${sysconfdir}
    install -m 0755 ${WORKDIR}/rescue-handler ${D}${sysconfdir}/rescue-handler

    device="/dev/mmcblk0p2"
    if [ "${MACHINE}" = "sl2619nand" ]; then
        device="/dev/ubi0_1"
    fi
    # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/astra-rescue.service ${D}${systemd_system_unitdir}/
    sed -i -e "s,@DEVICE@,${device},g" ${D}${systemd_system_unitdir}/astra-rescue.service
}

# Ensure files go into the main package
FILES:${PN} = "/rescue ${systemd_system_unitdir}/astra-rescue.service ${sysconfdir}/rescue-handler"

# Skip QA warnings about FHS non-standard paths
INSANE_SKIP:${PN} += "installed-vs-shipped"

# Enable systemd service
SYSTEMD_SERVICE:${PN} = "astra-rescue.service"
