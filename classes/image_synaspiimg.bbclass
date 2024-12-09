#/*******************************************************************************
#*
#*             Copyright 2020, Beechwoods Software, Inc.
#*             Copyright 2021 - 2022, Synaptics Incorporated.
#*
#*******************************************************************************/

inherit image_types synaimg_common

# This variable is available to request which values are suitable for IMAGE_FSTYPES
IMAGE_TYPES:append:syna = " \
    synaspiimg \
"

EXTRA_FW_DEPENDS = ""
EXTRA_FW_DEPENDS:dolphin = "synasdk-fw-enc:do_deploy"
EXTRA_FW_DEPENDS:platypus = "synasdk-fw-enc:do_deploy"
DEPENDS += "android-tools-native"


# by the do_deploy step of the bootloader package
do_rootfs[depends] += " \
    virtual/bootloader:do_deploy \
"

do_image_synaspiimg[depends] += " \
    e2fsprogs-native:do_populate_sysroot \
    vim-native:do_populate_sysroot \
    bc-native:do_populate_sysroot \
    synasdk-tools-native:do_populate_sysroot \
    virtual/bootloader:do_deploy \
    synasdk-preboot:do_deploy \
    syna-trusted-app:do_deploy \
    synasdk-security:do_deploy \
    synasdk-tzk:do_deploy \
    linux-syna:do_deploy \
    ${EXTRA_FW_DEPENDS} \
"

genx_spi_suboot_combo() {
  # Parse arguments
  local f_preboot
  local f_tee
  local f_bl
  local f_spi_combo


  f_preboot=$1; shift
  f_tee=$1; shift
  f_bl=$1; shift
  f_spi_combo=$1; shift

  # Pack preboot
  dd if=/dev/zero bs=1024 count=1 > $f_spi_combo
  cat $f_preboot >> $f_spi_combo

  preboot_size=`stat -c %s ${f_spi_combo}`
  padding_size=$[524288 - $preboot_size]

  f_PADDING=${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/dummy.bin
  dd if=/dev/zero of=$f_PADDING bs=1 count=$padding_size
  cat $f_PADDING >> $f_spi_combo

  # Pack TEE
  cat $f_tee >> $f_spi_combo

  tee_size=`stat -c %s ${f_spi_combo}`
  padding_size=$[1114112 - $tee_size]

  f_PADDING=${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/dummy.bin
  dd if=/dev/zero of=$f_PADDING bs=1 count=$padding_size
  cat $f_PADDING >> $f_spi_combo

  # Pack BL
  cat $f_bl >> $f_spi_combo

  bl_size=`stat -c %s ${f_spi_combo}`
  padding_size=$[2031616 - $bl_size]

  f_PADDING=${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/dummy.bin
  dd if=/dev/zero of=$f_PADDING bs=1 count=$padding_size
  cat $f_PADDING >> $f_spi_combo
  rm $f_PADDING
}

IMAGE_CMD:synaspiimg () {

# Create the SYNAIMG sub-directory
    if [ -d ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR} ]; then
        rm -rf ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}
    fi
    mkdir -p ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}

# Add a "tag"
    touch ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/TAG--${IMAGE_NAME}--TAG

# Make spi_suboot.bin which including preboot, tee and bootloader subimg
    genx_spi_suboot_combo ${DEPLOY_DIR_IMAGE}/preboot.subimg ${DEPLOY_DIR_IMAGE}/tee.subimg ${DEPLOY_DIR_IMAGE}/bootloader_nopreload.subimg ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/spi_suboot.bin

    cp ${DEPLOY_DIR_IMAGE}/linux_bootimgs.subimg ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/boot.subimg

# Workaround for build error:
    [ -d ${IMGDEPLOYDIR} ] || mkdir ${IMGDEPLOYDIR}
}
