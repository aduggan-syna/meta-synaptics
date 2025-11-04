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
    synasdk-security:do_deploy \
    synasdk-tzk:do_deploy \
    linux-syna:do_deploy \
    ${EXTRA_FW_DEPENDS} \
"

gen_preboot_subimg() {
  spi_vt_size=2048
  spi_header_size=1024

  . "${STAGING_DIR_NATIVE}/usr/share/syna/build/${SYNA_SDK_FLASH_TYPE_CFG_FILE}"

  f_input=$1; shift

  ### Fill the gap ###
  f_len=$(stat -c %s "${f_input}")
  if [ "$(expr "$f_len" + 2048)" -gt "${spi_boot_part_size}" ]; then
    exit 1
  fi

  ### Check if PMIC config is included ###
  expected_size_with_pmic=$(expr "${spi_boot_part_size}" - "${spi_vt_size}" - "${spi_header_size}")
  if [ $f_len -lt $expected_size_with_pmic ]; then
    echo "Warning: preboot_esmt.bin may be missing PMIC config (size: $f_len, expected: $expected_size_with_pmic)"
  fi

  fill_length=$(expr "${spi_boot_part_size}" - "${spi_vt_size}" - "${spi_header_size}" - "${f_len}") || true
  if [ $fill_length -gt 0 ]; then
    dd if=/dev/zero bs="$fill_length" count=1 >> "${f_input}"
  fi

  ### Append version table ###
  cat "${DEPLOY_DIR_IMAGE}/version_table" >> "${f_input}"

  ### Append to block size ###
  f_len=$(stat -c %s "${f_input}")
  if [ "$f_len" -gt "${spi_boot_part_size}" ]; then
    exit 1
  fi

  fill_length=$(expr "${spi_boot_part_size}" - "${spi_header_size}" - "${f_len}")
  if [ $fill_length -gt 0 ]; then
    dd if=/dev/zero bs="$fill_length" count=1 >> "${f_input}"
  fi
}

genx_spi_suboot_combo() {
  # Parse arguments
  f_preboot=$1; shift
  f_tee=$1; shift
  f_bl=$1; shift
  f_spi_combo=$1; shift

  # Dynamic partition size calculation
  f_spi_pt="${STAGING_DIR_NATIVE}/usr/share/syna/build/${SYNA_SDK_PT_FILE}"
  spi_preboot_end=$(awk '$2 == "preboot_a" { sub(/K$/, "", $1); print $1 * 1024 }' "$f_spi_pt")
  spi_tzk_size=$(awk '$2 == "tzk_a" { sub(/K$/, "", $1); print $1 * 1024 }' "$f_spi_pt")
  spi_tzk_end=$(expr "$spi_preboot_end" + "$spi_tzk_size")
  spi_bl_size=$(awk '$2 == "bl_a" { sub(/K$/, "", $1); print $1 * 1024 }' "$f_spi_pt")
  spi_bl_end=$(expr "$spi_tzk_end" + "$spi_bl_size")

  # Pack preboot
  dd if=/dev/zero bs=1024 count=1 > "$f_spi_combo"
  cat "$f_preboot" >> "$f_spi_combo"

  preboot_size=$(stat -c %s "${f_spi_combo}")
  padding_size=$(expr "$spi_preboot_end" - "$preboot_size" || true)

  f_PADDING="${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/dummy.bin"
  dd if=/dev/zero of="$f_PADDING" bs=1 count="$padding_size"
  cat "$f_PADDING" >> "$f_spi_combo"

  # Pack TEE
  cat "$f_tee" >> "$f_spi_combo"

  tee_size=$(stat -c %s "${f_spi_combo}")
  padding_size=$(expr "$spi_tzk_end" - "$tee_size" || true)

  dd if=/dev/zero of="$f_PADDING" bs=1 count="$padding_size"
  cat "$f_PADDING" >> "$f_spi_combo"

  # Pack BL
  cat "$f_bl" >> "$f_spi_combo"

  bl_size=$(stat -c %s "${f_spi_combo}")
  padding_size=$(expr "$spi_bl_end" - "$bl_size" || true)

  dd if=/dev/zero of="$f_PADDING" bs=1 count="$padding_size"
  cat "$f_PADDING" >> "$f_spi_combo"
  rm -f "$f_PADDING"
}

IMAGE_CMD:synaspiimg () {

# Create the SYNAIMG sub-directory
    if [ -d "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}" ]; then
        rm -rf "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}"
    fi
    mkdir -p "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}"

# Add a "tag"
    touch "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/TAG--${IMAGE_NAME}--TAG"

# Preserve original file so we don't keep growing it
    cp "${DEPLOY_DIR_IMAGE}/preboot.subimg" "${DEPLOY_DIR_IMAGE}/preboot_aligned.subimg"

# Align preboot subimg
    gen_preboot_subimg "${DEPLOY_DIR_IMAGE}/preboot_aligned.subimg"

# Make spi_suboot.bin which including preboot, tee and bootloader subimg
    genx_spi_suboot_combo "${DEPLOY_DIR_IMAGE}/preboot_aligned.subimg" "${DEPLOY_DIR_IMAGE}/tee.subimg" "${DEPLOY_DIR_IMAGE}/bootloader_nopreload.subimg" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/spi_suboot.bin"

    cp "${DEPLOY_DIR_IMAGE}/linux_bootimgs.subimg" "${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/boot.subimg"

# Workaround for build error:
    if [ ! -d "${IMGDEPLOYDIR}" ]; then
        mkdir "${IMGDEPLOYDIR}"
    fi
}
