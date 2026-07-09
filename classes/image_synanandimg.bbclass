#/*******************************************************************************
#*
#*             Copyright 2020, Beechwoods Software, Inc.
#*             Copyright 2021 - 2022, Synaptics Incorporated.
#*
#*******************************************************************************/

inherit image_types synaimg_common
require recipes-devtools/synasdk/synasdk-config.inc

# This variable is available to request which values are suitable for IMAGE_FSTYPES
IMAGE_TYPES:append:syna = " \
    synanandimg \
"

SYNAREALMACH:sl2619nand = "sl2619"
EXTRA_FW_DEPENDS = ""
EXTRA_FASTLOGO_DEPENDS = "synasdk-fastlogo:do_deploy"
EXTRA_FASTLOGO_DEPENDS:klamath = ""

DEPENDS += "android-tools-native"


do_image_synanandimg[depends] += " \
    e2fsprogs-native:do_populate_sysroot \
    vim-native:do_populate_sysroot \
    bc-native:do_populate_sysroot \
    coreutils-native:do_populate_sysroot \
    mtd-utils-native:do_populate_sysroot \
    synasdk-tools-native:do_populate_sysroot \
    virtual/bootloader:do_deploy \
    synasdk-preboot:do_deploy \
    synasdk-security:do_deploy \
    synasdk-tzk:do_deploy \
    linux-syna:do_deploy \
    ${EXTRA_FW_DEPENDS} \
    ${EXTRA_FASTLOGO_DEPENDS} \
"

nand_gen_subimg_info() {
  subimg_name=$1

  # Retrieve layout of the subimage
  local subimg_layout
  subimg_layout=`cat ${DEPLOY_DIR_IMAGE}/subimglayout | grep "^${subimg_name}"`
  [ "x${subimg_layout}" != "x" ]

  v_start_block_index=`echo ${subimg_layout}|awk '{print $2}'`
  v_num_blocks=`echo ${subimg_layout}|awk '{print $3}'`
  v_data_type=`echo ${subimg_layout}|awk '{print $4}'`
  v_partition_type=`echo ${subimg_layout}|awk '{print $5}'`

  [ "x${v_start_block_index}" != "x" ]
  [ "x${v_num_blocks}" != "x" ]
  [ "x${v_data_type}" != "x" ]
  [ "x${v_partition_type}" != "x" ]

  gen_subimg_info  --name ${subimg_name} \
                   --major "0" --minor "0" \
                   --reserved_blocks 0 \
                   --start_blkind ${v_start_block_index} \
                   --num_blocks ${v_num_blocks} \
                   --data_type ${v_data_type} \
                   --partition_type ${v_partition_type} \
                   --output ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/subimgs/${subimg_name}.subimg.info \
                   ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/subimgs/${subimg_name}.subimg
}

mkfs_ubifs() {
  ubifs_args="$1 -x zlib"
  ubinize_args="$2"
  vol_size="$3"
  rootfs_dir="$4"
  subimg_name="$5"

  [ "$ubifs_args" != "" ]
  [ "$ubinize_args" != "" ]
  [ "$vol_size" != "" ]
  [ "$rootfs_dir" != "" ]
  [ "$subimg_name" != "" ]
  mkfs.ubifs  -F -r ${rootfs_dir} ${ubifs_args} -o ${DEPLOY_DIR_IMAGE}/${subimg_name}.ubifs

  ubinize_cfg=${DEPLOY_DIR_IMAGE}/${subimg_name}-ubinize.cfg
  if [ "${subimg_name}" = "rescue" ]; then
    echo "[uboot-env-volume]" > ${ubinize_cfg}
    echo "mode=ubi" >> ${ubinize_cfg}
    echo "vol_id=0" >> ${ubinize_cfg}
    echo "vol_size=16KiB" >> ${ubinize_cfg}
    echo "vol_type=dynamic" >> ${ubinize_cfg}
    echo "vol_name=uboot_env" >> ${ubinize_cfg}

    echo "[uboot-env-r-volume]" >> ${ubinize_cfg}
    echo "mode=ubi" >> ${ubinize_cfg}
    echo "vol_id=1" >> ${ubinize_cfg}
    echo "vol_size=16KiB" >> ${ubinize_cfg}
    echo "vol_type=dynamic" >> ${ubinize_cfg}
    echo "vol_name=uboot_env_r" >> ${ubinize_cfg}

    echo "[${subimg_name}-volume]" >> ${ubinize_cfg}
    echo "mode=ubi" >> ${ubinize_cfg}
    echo "image=${DEPLOY_DIR_IMAGE}/${subimg_name}.ubifs" >> ${ubinize_cfg}
    echo "vol_id=2" >> ${ubinize_cfg}
  else
    echo "[${subimg_name}-volume]" > ${ubinize_cfg}
    echo "mode=ubi" >> ${ubinize_cfg}
    echo "image=${DEPLOY_DIR_IMAGE}/${subimg_name}.ubifs" >> ${ubinize_cfg}
    echo "vol_id=0" >> ${ubinize_cfg}
  fi
  echo "vol_size=${vol_size}MiB" >> ${ubinize_cfg}
  echo "vol_type=dynamic" >> ${ubinize_cfg}
  echo "vol_name=${subimg_name}" >> ${ubinize_cfg}
  echo "vol_flags=autoresize" >> ${ubinize_cfg}

  ubinize -vv -o ${DEPLOY_DIR_IMAGE}/${subimg_name}.subimg ${ubinize_args} ${ubinize_cfg}
}

unand_gen_images() {
  unandimg_name=$1
  list_parts=$2

  workdir_subimgs=${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/subimgs
  workdir_release=${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}

  [ "x${unandimg_name}" != "x" ]

  day=`date +%Y%m%d`
  time=`date +%H%M`
  cmd_args=" -d ${workdir_subimgs}"
  cmd_args="${cmd_args} -p ${CONFIG_NAND_PAGE_SIZE}"
  cmd_args="${cmd_args} -b ${CONFIG_NAND_BLOCK_SIZE}"
  cmd_args="${cmd_args} -c ${CONFIG_NAND_TOTAL_SIZE}"
  cmd_args="${cmd_args} -j ${day}"
  cmd_args="${cmd_args} -n ${time}"
  cmd_args="${cmd_args} --cpu_type A0"
  cmd_args="${cmd_args} --ddr_type DDR3"
  cmd_args="${cmd_args} --ddr_channel DDR_DUAL_CHANNEL"
  cmd_args="${cmd_args} -o ${workdir_release}/${unandimg_name}.img"

  for p in ${list_parts}; do
    cmd_args="${cmd_args} $p"
  done
  gen_uniimg ${cmd_args}
}

unand_shrink_part() {
  local unandimg_name
  local list_parts
  local shrink_part

  unandimg_name=$1; shift
  list_parts=$1; shift
  shrink_part=$1; shift

  outdir_product_release_unand=${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}
  if [ ! -f "${outdir_product_release_unand}/subimgs/${shrink_part}.subimg" ]; then
    echo "WARN: can't find $shrink_part in subimgs"
    return 0
  fi

  last_item=$(echo "$list_parts" | tail -n 1 | awk '{print $1}')

  if [ "$(basename "$shrink_part")" == "$last_item" ]; then
    file_size=$(stat -c%s "${outdir_product_release_unand}/subimgs/${shrink_part}.subimg")

    if [ $(stat -c%s "${outdir_product_release_unand}/${unandimg_name}.img") -ge "$file_size" ]; then
      truncate -s "-$file_size" "${outdir_product_release_unand}/${unandimg_name}.img"
    else
      echo "ERROR: ${unandimg_name}.img size smaller than $shrink_part"
      exit 1
    fi
  else
    echo "$shrink_part is not the last item of list"
  fi
}

IMAGE_CMD:synanandimg () {
# Check that the needed files are available
    [ -f ${DEPLOY_DIR_IMAGE}/key.subimg ]
    [ -f ${DEPLOY_DIR_IMAGE}/preboot.subimg ]
    [ -f ${DEPLOY_DIR_IMAGE}/bootloader_nopreload.subimg ]
    [ -f ${DEPLOY_DIR_IMAGE}/tee.subimg ]
    [ -f ${DEPLOY_DIR_IMAGE}/tee_recovery.subimg ]
    [ -f ${DEPLOY_DIR_IMAGE}/linux_bootimgs.subimg ]

    . ${CONFIG_FILE}

# Append the preload_ta to the bootloader
    # Align bootloader.subimg to 512B
    bootloader_subimg_size=`stat -c %s ${DEPLOY_DIR_IMAGE}/bootloader_nopreload.subimg`
    bootloader_append_size=`expr 512 - ${bootloader_subimg_size} % 512`

    cp ${DEPLOY_DIR_IMAGE}/bootloader_nopreload.subimg ${DEPLOY_DIR_IMAGE}/bootloader.subimg

    if [ ${bootloader_append_size} -lt 512 ]; then
        dd if=/dev/zero of=${DEPLOY_DIR_IMAGE}/bootloader.subimg bs=1 seek=${bootloader_subimg_size} count=${bootloader_append_size} conv=notrunc
    fi
    if [ -f ${DEPLOY_DIR_IMAGE}/preload_ta.subimg ];then
        cat ${DEPLOY_DIR_IMAGE}/preload_ta.subimg >> ${DEPLOY_DIR_IMAGE}/bootloader.subimg
    fi

# Create the SYNAIMG sub-directory
    if [ -d ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR} ]; then
        rm -rf ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}
    fi
    mkdir -p ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}

# Add a "tag"
    touch ${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/TAG--${IMAGE_NAME}--TAG




# Generate the deployed image
    subimg_list=`cat ${DEPLOY_DIR_IMAGE}/subimglayout | awk '{print $1}'`
    mapping_list="/block0/block0 \
                  /pre-bootloader/preboot /preboot/preboot \
                  /key_a/key /key_b/key \
                  /cmboot_a/cmboot /cmboot_b/cmboot \
                  /rescue/rescue \
                  /rootfs_a/rootfs /rootfs_b/rootfs \
                  /opt/opt /home/home /tsb/tsb /app/app \
                  /firmware_a/firmware /firmware_b/firmware \
                  /fastlogo/fastlogo /fastlogo_a/fastlogo /fastlogo_b/fastlogo"

    SUBIMG_DIR=${DEPLOY_DIR_IMAGE}/${SYNAIMG_DEPLOY_SUBDIR}/subimgs
    mkdir -p ${SUBIMG_DIR}
    cp ${DEPLOY_DIR_IMAGE}/subimglayout ${SUBIMG_DIR}

#Generate block0 subimg
    num_blk_num=`expr ${CONFIG_NAND_BOOT_PART_SIZE} / ${CONFIG_NAND_BLOCK_SIZE}`
    klamath-mk_nandblock0_image --page-size=${CONFIG_NAND_PAGE_SIZE} \
                        --block-size=${CONFIG_NAND_BLOCK_SIZE} \
                        --ecc-strength=${CONFIG_NAND_ECC_STRENGTH} \
                        --nand-scrambler-en 0 \
                        --nand-blk-num=${num_blk_num} \
                        ${DEPLOY_DIR_IMAGE}/block0.subimg

# Merge tee, bootloader and sysmgr into cmboot.subimg
    params=" -i TZK* -d ${DEPLOY_DIR_IMAGE}/tee.subimg -i BTLR -d ${DEPLOY_DIR_IMAGE}/bootloader.subimg"
    if [ -e ${DEPLOY_DIR_IMAGE}/sysmgr.subimg ]; then
        params=${params}" -i SMGR -d ${DEPLOY_DIR_IMAGE}/sysmgr.subimg"
    fi
    genimg -n cmboot -A 4096 $params -o ${DEPLOY_DIR_IMAGE}/cmboot_noinfo.subimg
    if [ "is${CONFIG_NO_PREPEND_IMG_INFO}" = "isy" ]; then
        cp ${DEPLOY_DIR_IMAGE}/cmboot_noinfo.subimg ${DEPLOY_DIR_IMAGE}/cmboot.subimg
    else
        prepend_image_info.sh ${DEPLOY_DIR_IMAGE}/cmboot_noinfo.subimg ${DEPLOY_DIR_IMAGE}/cmboot.subimg
    fi

    leb=`expr ${CONFIG_NAND_BLOCK_SIZE} - ${CONFIG_NAND_PAGE_SIZE} \* 2`
    mtbd_size2vol_size=`expr 1024 \* 1024 / ${CONFIG_NAND_BLOCK_SIZE}`

# Make rescue
    if [ "${ENABLE_RESCUE_MODE}" = "1" ]; then
        rescue_size=`grep -m1 rescue ${DEPLOY_DIR_IMAGE}/subimglayout | awk '{print $3}'`
        #reserve 4% size for eraseblocks
        max_erase_blks=`expr ${rescue_size} - ${rescue_size} / 100 \* 4`
        vol_size=`expr ${max_erase_blks} / ${mtbd_size2vol_size}`
        rm -rf ${DEPLOY_DIR_IMAGE}/rescue
        mkdir ${DEPLOY_DIR_IMAGE}/rescue
        cp ${IMAGE_ROOTFS}/rescue/Image-${MACHINE}.gz ${DEPLOY_DIR_IMAGE}/rescue/Image.gz
        cp ${IMAGE_ROOTFS}/rescue/rescue-${MACHINE}.dtb ${DEPLOY_DIR_IMAGE}/rescue/board.dtb
        cp ${IMAGE_ROOTFS}/rescue/rescue-${MACHINE}.rootfs.cpio.gz ${DEPLOY_DIR_IMAGE}/rescue/initramfs.rootfs.cpio.gz
        #make ubifs rescue
        mkfs_ubifs " -e ${leb} -c ${max_erase_blks} -m ${CONFIG_NAND_PAGE_SIZE}" " -vv -m ${CONFIG_NAND_PAGE_SIZE} -p ${CONFIG_NAND_BLOCK_SIZE} -s ${CONFIG_NAND_PAGE_SIZE}" "${vol_size}" "${DEPLOY_DIR_IMAGE}/rescue" "rescue"
    fi

# Make rootfs
    rootfs_size=`grep -m1 rootfs ${DEPLOY_DIR_IMAGE}/subimglayout | awk '{print $3}'`
    #reserve 4% size for eraseblocks
    max_erase_blks=`expr ${rootfs_size} - ${rootfs_size} / 100 \* 4`
    #add boot subimg to rootfs
    rm -rf ${IMAGE_ROOTFS}/boot
    mkdir ${IMAGE_ROOTFS}/boot
    cp ${DEPLOY_DIR_IMAGE}/linux_bootimgs.subimg ${IMAGE_ROOTFS}/boot

    #zip large linux drivers
    large_drivers="synaptics/net/wireless"
    for driver in ${large_drivers}; do
        find ${IMAGE_ROOTFS}/${nonarch_base_libdir}/modules/${PREFERRED_VERSION_linux-syna}/kernel/drivers/${driver} -name "*.ko" | \
        xargs xz
        find ${IMAGE_ROOTFS}/${nonarch_base_libdir}/modules/${PREFERRED_VERSION_linux-syna}/kernel/drivers/${driver} -name *.xz -exec sh -c 'mv "$1" "${1%.xz}"' _ {} \;
    done

    vol_size=`expr ${max_erase_blks} / ${mtbd_size2vol_size}`

    #make ubifs rootfs
    mkfs_ubifs " -e ${leb} -c ${max_erase_blks} -m ${CONFIG_NAND_PAGE_SIZE}" " -vv -m ${CONFIG_NAND_PAGE_SIZE} -p ${CONFIG_NAND_BLOCK_SIZE} -s ${CONFIG_NAND_PAGE_SIZE} --image-seq=695949396" "${vol_size}" "${IMAGE_ROOTFS}" "rootfs"

    for i in ${subimg_list}; do
        subimg_name=`echo $mapping_list | grep -o "/${i}\(_[a|b]\)\?/[[:alnum:]_-]*" | head -1 | cut -d / -f3`
        if [ -n "$subimg_name" ] && [ -f ${DEPLOY_DIR_IMAGE}/$subimg_name.subimg ]; then
            cp ${DEPLOY_DIR_IMAGE}/$subimg_name.subimg ${SUBIMG_DIR}/$i.subimg
            if [ "$i" = "pre-bootloader" ]; then
                f_input=${SUBIMG_DIR}/$i.subimg
                ### Fill the gap ###
                f_len=`stat -c %s ${f_input}`
                [ ! `expr $f_len + 2048` -gt ${CONFIG_NAND_BOOT_PART_SIZE} ]
                fill_length=`expr ${CONFIG_NAND_BOOT_PART_SIZE}  - 2048 - ${f_len}`
                dd if=/dev/zero bs=$fill_length count=1 >> ${f_input}

                ### Append version table ###
                cat ${DEPLOY_DIR_IMAGE}/version_table >> ${f_input}

                ### Append to block size ###
                f_len=$(stat -c %s ${f_input})
                [ ! $f_len -gt ${CONFIG_NAND_BOOT_PART_SIZE} ]
                fill_length=`expr ${CONFIG_NAND_BOOT_PART_SIZE} - ${f_len}`
                dd if=/dev/zero bs=$fill_length count=1 >> ${f_input}
            fi
            nand_gen_subimg_info $i
        else
            echo "image is $i, mapping name is $subimg_name, ERROR!"
            exit 1
        fi
    done
    unand_gen_images "uNAND_full" "${subimg_list}"
    unand_shrink_part "uNAND_full" "${subimg_list}" "rootfs_b"
}
