#/*******************************************************************************
#*
#*             Copyright 2020, Beechwoods Software, Inc.
#*             Copyright 2021 - 2022, Synaptics Incorporated.
#*
#*******************************************************************************/

inherit image_types synaimg_common

# This variable is available to request which values are suitable for IMAGE_FSTYPES
IMAGE_TYPES:append:syna = " \
    synausbimg \
"

SYNAREALMACH:platypus = "sl1640"
SYNAREALMACH:myna2 = "sl1620"
SYNAREALMACH:sl1680 = "sl1680"

EXTRA_FW_DEPENDS = ""
EXTRA_FW_DEPENDS:dolphin = "synasdk-fw-enc:do_deploy"
EXTRA_FW_DEPENDS:platypus = "synasdk-fw-enc:do_deploy"
FASTLOGO_DEPENDS = "virtual/bootloader:do_deploy"
FASTLOGO_DEPENDS:klamath = ""
DEPENDS += "android-tools-native"


# by the do_deploy step of the bootloader package
do_rootfs[depends] += " \
    virtual/bootloader:do_deploy \
"

do_image_synausbimg[depends] += " \
    e2fsprogs-native:do_populate_sysroot \
    vim-native:do_populate_sysroot \
    bc-native:do_populate_sysroot \
    synasdk-tools-native:do_populate_sysroot \
    virtual/bootloader:do_deploy \
    synasdk-preboot:do_deploy \
    synasdk-security:do_deploy \
    synasdk-tzk:do_deploy \
    linux-syna:do_deploy \
    ${EXTRA_FW_DEPENDS} \
    ${FASTLOGO_DEPENDS} \
"

IMAGE_CMD:synausbimg () {

# Check that the needed files are available
    if [ "${MACHINE}" = "sl2619usb" ] || [ "${MACHINE}" = "sl2619-coralboard-usb" ]; then
        [ -f "${DEPLOY_DIR_IMAGE}/key.bin" ]
        [ -f "${DEPLOY_DIR_IMAGE}/spk.bin" ]
        [ -f "${DEPLOY_DIR_IMAGE}/m52bl.bin" ]
        [ -f "${DEPLOY_DIR_IMAGE}/sysmgr.subimg" ]
    else
        [ -f "${DEPLOY_DIR_IMAGE}/gen3_erom.bin.usb" ]
        [ -f "${DEPLOY_DIR_IMAGE}/gen3_scs.bin.usb" ]
        [ -f "${DEPLOY_DIR_IMAGE}/gen3_bkl.bin.usb" ]
        [ -f "${DEPLOY_DIR_IMAGE}/gen3_boot_monitor.bin.usb" ]
        [ -f "${DEPLOY_DIR_IMAGE}/gen3_scs_param.bin.usb" ]
        [ -f "${DEPLOY_DIR_IMAGE}/gen3_sysinit.bin.usb" ]
        [ -f "${DEPLOY_DIR_IMAGE}/gen3_miniloader.bin.usb" ]
        [ -f "${DEPLOY_DIR_IMAGE}/Image-${MACHINE}.bin" ]
        [ -f "${DEPLOY_DIR_IMAGE}/core-image-initramfs-boot-${MACHINE}.cpio.gz" ]
    fi    

    [ -f "${DEPLOY_DIR_IMAGE}/bootloader_nopreload.subimg" ]
    [ -f "${DEPLOY_DIR_IMAGE}/tee.subimg" ]

# Create the SYNAIMG sub-directory
    if [ -d "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}" ]; then
        rm -rf "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}"
    fi
    mkdir -p "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}"

# Add a "tag"
    touch "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/TAG--${IMAGE_NAME}--TAG"

    if [ "${MACHINE}" = "sl2619usb" ] || [ "${MACHINE}" = "sl2619-coralboard-usb" ]; then
        cp "${DEPLOY_DIR_IMAGE}/bootloader_nopreload.subimg" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/bl.subimg"
        cp "${DEPLOY_DIR_IMAGE}/tee.subimg" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/tzk.subimg"
        cp "${DEPLOY_DIR_IMAGE}/key.bin" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/key.bin"
        cp "${DEPLOY_DIR_IMAGE}/spk.bin" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/spk.bin"
        cp "${DEPLOY_DIR_IMAGE}/m52bl.bin" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/m52bl.bin"
        cp "${DEPLOY_DIR_IMAGE}/sysmgr.subimg" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/sysmgr.subimg"
    else
        cp "${DEPLOY_DIR_IMAGE}/gen3_erom.bin.usb" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/"
        cp "${DEPLOY_DIR_IMAGE}/gen3_scs.bin.usb" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/"
        cp "${DEPLOY_DIR_IMAGE}/gen3_bkl.bin.usb" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/"
        cp "${DEPLOY_DIR_IMAGE}/gen3_boot_monitor.bin.usb" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/"
        cp "${DEPLOY_DIR_IMAGE}/gen3_scs_param.bin.usb" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/"
        cp "${DEPLOY_DIR_IMAGE}/gen3_sysinit.bin.usb" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/"
        cp "${DEPLOY_DIR_IMAGE}/gen3_miniloader.bin.usb" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/"
        cp "${DEPLOY_DIR_IMAGE}/bootloader_nopreload.subimg" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/gen3_uboot.bin.usb"
        dd if="${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/gen3_uboot.bin.usb" of="${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/gen3_uboot.bin.usb.header" bs=1 count=512
        cp "${DEPLOY_DIR_IMAGE}/tee.subimg" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/gen3_tzk.bin.usb"
        dd if="${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/gen3_tzk.bin.usb" of="${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/gen3_tzk.bin.usb.header" bs=1 count=512
    fi
    gzip -c -1 "${DEPLOY_DIR_IMAGE}/Image-${MACHINE}.bin" > "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/Image.gz"
    for f in "${DEPLOY_DIR_IMAGE}"/*.dtb
    do
        if [ ! -L "$f" ]; then
            dtb_file_name=$(basename "$f")
           cp "$f" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/${dtb_file_name}"
        fi
    done

    cp "${DEPLOY_DIR_IMAGE}/core-image-initramfs-boot-${MACHINE}.cpio.gz" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/ramdisk.cpio.gz"
    cp "${DEPLOY_DIR_IMAGE}/manifest.yaml" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/manifest.yaml"

    if [ "${MACHINE}" = "sl1680usb" ]; then
        cp "${DEPLOY_DIR_IMAGE}/gen3_ddr_phy_fw_0.bin.usb" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/"
        cp "${DEPLOY_DIR_IMAGE}/gen3_ddr_phy_fw_1.bin.usb" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/"
    fi

    if [ ! -d "${IMGDEPLOYDIR}" ]; then
        mkdir "${IMGDEPLOYDIR}"
    fi
}
