# Copyright 2020, Beechwoods Software, Inc.
# Copyright 2021, Synaptics Incorporated

DESCRIPTION = "Linux Kernel for Synaptics STB SoCs"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

INITRAMFS_IMAGE = "core-image-initramfs-boot"

require recipes-kernel/linux/linux-yocto.inc

EXTRA_KERNEL_CONFIGME_DEPENDS = ""
do_kernel_configme[depends] += "${EXTRA_KERNEL_CONFIGME_DEPENDS}"

# No extensions to the linux kernel version
LINUX_VERSION_EXTENSION = ""

KERNEL_DEVICETREE = "${SYNA_KERNEL_DTS_FILE}  \
                     ${SYNA_KERNEL_DTBO_FILE} \
                     "

KERNEL_DTC_FLAGS += "-@"

COMPATIBLE_MACHINE = "syna"

DEPENDS:append = " lz4-native"
DEPENDS:append:syna = " \
    synasdk-tools-native \
    synasdk-security-native \
    synasdk-config-native \
    vim-native \
"

CMDLINE_EMMC_BOOT = "console=ttyS0,115200 rootwait rootfstype=ext4"
CMDLINE_EMMC_BOOT:sl1620usb = "shell earlycon console=ttyS0,115200 rootwait rootfstype=ext4"
CMDLINE_EMMC_BOOT:sl1640usb = "shell earlycon console=ttyS0,115200 rootwait rootfstype=ext4"
CMDLINE_EMMC_BOOT:sl1680usb = "shell earlycon console=ttyS0,115200 rootwait rootfstype=ext4"
CMDLINE_EMMC_BOOT:sl1680spi = "shell earlycon console=ttyS0,115200 rootwait rootfstype=ext4"
CMDLINE_EMMC_BOOT:sl1620spi = "shell earlycon console=ttyS0,115200 rootwait rootfstype=ext4"
CMDLINE_EMMC_BOOT:sl1640spi = "shell earlycon console=ttyS0,115200 rootwait rootfstype=ext4"
CMDLINE_EMMC_BOOT:append = "${@bb.utils.contains('KGDB_ENABLE', '1', ' kgdboc=ttyS0,115200', '', d)}"
CMDLINE_EMMC_BOOT:append = "${@bb.utils.contains('KGDB_WAIT', '1', ' kgdbwait', '', d)}"

# Set the verbosity of kernel messages during runtime
# You can define CMDLINE_DEBUG in your local.conf or distro.conf to override this behaviour
# Possible values: "quiet", "debug" or ""
# Defaults to "" because "debug" is very noisy with systemd
CMDLINE_DEBUG ?= ""
CMDLINE_EMMC_BOOT:append = " ${CMDLINE_DEBUG}"

CMDLINE:syna = "${CMDLINE_EMMC_BOOT}"

require recipes-devtools/synasdk/synasdk-config.inc

genx_secure_image() {
  v_image_type=$1; shift
  in_extras=$1; shift
  in_length=$1; shift
  f_input=$1; shift
  f_output=$1; shift

  ### Check input file ###
  [ -f $f_input ]

  ### Exectuable for generating secure image ###
  exec_cmd=gen_x_secure_image

  ### Prepare arguments ###
  unset exec_args

  # Other parameters
  exec_args="${exec_args} --chip-name=${syna_chip_name}"
  exec_args="${exec_args} --chip-rev=${syna_chip_rev}"
  exec_args="${exec_args} --img_type=$v_image_type"
  exec_args="${exec_args} --key_type=ree"

  #exec_args="${exec_args} --seg_id=0x00000000"
  #exec_args="${exec_args} --seg_id_mask=0xFFFFFFFF"
  #exec_args="${exec_args} --version=0x00000001"
  #exec_args="${exec_args} --version_mask=0xFFFFFFFF"
  exec_args="${exec_args} --length=$in_length"
  exec_args="${exec_args} --extras=$in_extras"

  exec_args="${exec_args} --workdir-security-tools=${security_tools_path}"
  exec_args="${exec_args} --workdir-security-keys=${security_keys_path}"
  # Input and output
  exec_args="${exec_args} --in_payload=${f_input} --out_store=${f_output}"

  ### Generate secure image ###
  eval ${exec_cmd} "${exec_args}"
}

do_compile:append() {

    unset DTC_FLAGS;

    for dtbf in ${SYNA_KERNEL_DTBO_FILE}; do
        dtb=`normalize_dtb "$dtbf"`
        oe_runmake $dtb CC="${KERNEL_CC} $cc_extra " LD="${KERNEL_LD}" ${KERNEL_EXTRA_ARGS}
    done
}


do_deploy:append() {

    dtb_name=${SYNA_KERNEL_DTS_FILE}
    dtb_name=${dtb_name##*/}
    dtb_name=${dtb_name%%.*}
    echo "dtb_name = $dtb_name"

    . ${CONFIG_FILE}

    . ${CHIP_RC_FILE}

    security_tools_path="${STAGING_DIR_NATIVE}${prefix}/libexec/syna/"
    security_keys_path="${STAGING_DATADIR_NATIVE}/syna/keys/${syna_chip_name}/${syna_chip_rev}"

    # Compress the kernel with lz4
    cat ${DEPLOYDIR}/${KERNEL_IMAGETYPE}-initramfs-${MACHINE}.bin | lz4 -f -9 -B4 > ${D}/boot/${KERNEL_IMAGETYPE}-${PV}.lz4
    # Combine Kernel Image and Device Tree in a single image
    genimg -n boot -V 0 -v 0 -i LNXB -d ${D}/boot/${KERNEL_IMAGETYPE}-${PV}.lz4 -i LDTB -d ${D}/boot/${dtb_name}.dtb -o ${DEPLOYDIR}/lz4Image-dtb.${dtb_name}
    # Pack with mkbootimg
    mkbootimg --kernel ${DEPLOYDIR}/lz4Image-dtb.${dtb_name} --cmdline "${CMDLINE}" --base 0x05000000 --output ${DEPLOYDIR}/linux_bootimgs_raw.bin
    # Generate Secure Image
    if [ "is${CONFIG_GENX_ENABLE}" = "isy" ]; then
        ${security_tools_path}in_extras.py "LINUX_KERNEL" ${DEPLOYDIR}/in_linux_kernel.bin 0x00000001
        #call internal function
        genx_secure_image "LINUX_KERNEL" ${DEPLOYDIR}/in_linux_kernel.bin 0x0 ${DEPLOYDIR}/linux_bootimgs_raw.bin ${DEPLOYDIR}/linux_bootimgs_en.bin
    else
        gen_secure_image --code-type=5 --add-custk-store=0 --add-ersak-store=0 --chip-name=${syna_chip_name} --chip-rev=${syna_chip_rev} --market-id=`printf 0x%x ${syna_chip_mid}` --workdir-security-tools=${security_tools_path} --workdir-security-keys=${security_keys_path}  --input-file=${DEPLOYDIR}/linux_bootimgs_raw.bin --output-file=${DEPLOYDIR}/linux_bootimgs_en.bin
    fi
    # Generate subimg
    if [ "is${CONFIG_NO_PREPEND_IMG_INFO}" = "isy" ]; then
        cp ${DEPLOYDIR}/linux_bootimgs_en.bin ${DEPLOYDIR}/linux_bootimgs.subimg
    else
        prepend_image_info.sh ${DEPLOYDIR}/linux_bootimgs_en.bin ${DEPLOYDIR}/linux_bootimgs.subimg
    fi
    # Cleanup intermediate files
    rm -f ${DEPLOYDIR}/lz4Image-dtb.${dtb_name} ${DEPLOYDIR}/lz4Image-dtb.${dtb_name}.header ${DEPLOYDIR}/linux_bootimgs_raw.bin ${DEPLOYDIR}/linux_bootimgs_en.bin
}

# Kernel modules for Platypus and Dolphin
KERNEL_MODULE_AUTOLOAD:append = " \
    amp_bm \
    vpu \
    rpi-panel-attiny-regulator \
    panel-waveshare-dsi \
    syna_drm \
    pvrsrvkm \
    bcmdhd \
"

KERNEL_MODULE_AUTOLOAD:append:dolphin = " \
    isp \
    vvcam_vb \
    vvcam_isp \
    vvcam_video \
    vvcam_isp_subdev \
    hrx_v4l2 \
"

KERNEL_MODULE_PROBECONF:append = "bcmdhd "
KERNEL_MODULE_PROBECONF:append = "syna_drm "
KERNEL_MODULE_PROBECONF:append = "rpi-panel-attiny-regulator "
KERNEL_MODULE_PROBECONF:append = "panel-waveshare-dsi "
KERNEL_MODULE_PROBECONF:append = "amp_bm "
KERNEL_MODULE_PROBECONF:append = "pvrsrvkm "
KERNEL_MODULE_PROBECONF:append = "syna-vpu "
KERNEL_MODULE_PROBECONF:append = "vpu "

module_conf_syna_drm = "softdep syna_drm pre: amp_bm fxl6408 rpi-panel-attiny-regulator panel-waveshare-dsi"
module_conf_pvrsrvkm = "softdep pvrsrvkm pre: syna_drm"

module_conf_syna-vpu = "softdep syna-vpu pre: amp_bm"
module_conf_vpu = "softdep vpu pre: amp_bm"

# module_conf_bcmdhd = "options bcmdhd firmware_path=bcm/fw_bcm43752.bin nvram_path=bcm/bcmdhd_43752.cal clm_path=bcm/bcmdhd_clm_43752.blob"
