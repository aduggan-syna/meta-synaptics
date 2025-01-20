IMAGE_FEATURES:append = " debug-tweaks package-management ssh-server-dropbear"

TEE_TZK= " \
    tee-supplicant \
    synasdk-tee \
"

TEE_OPTEE = " \
    optee-client \
    optee-os-tadevkit \
    optee-examples \
    optee-test \
"

IMAGE_INSTALL:append = " \
    gdbserver \
    curl \
    kernel-modules \
    linux-firmware-syna \
    i2c-tools \
    synasdk-drivers-axi-meter \
    synasdk-drivers-bluetooth-lpm \
    synasdk-drivers-bluetooth-rfkill \
    synasdk-drivers-hl7593 \
    synasdk-drivers-rt5739 \
    synasdk-drivers-tps6286x \
    synasdk-drivers-dwc3-syna \
    synasdk-drivers-phy-syna-usb \
    synasdk-drivers-berlin-chipid \
    synasdk-macaddr \
    ethtool \
    syna-mount-generator \
    syna-wifi-tools \
    synasdk-bootctrl \
    swupdate \
    swupdate-client \
    swupdate-progress \
    swupdate-tools \
    swupdate-tools-hawkbit \
    swupdate-tools-ipc \
    libubootenv \
    libubootenv-bin \
    swu-conf \
"

IMAGE_INSTALL:append:myna2 = " \
    ${TEE_OPTEE} \
    synasdk-drivers-tlc5917 \
    brcm-patchram-plus \
    synasdk-brcm-bt-start \
    syna-trusted-app \
"

PLATYPUS_DOLPHIN_INSTALL = " \
    ${TEE_OPTEE} \
    synasdk-drivers-sm \
    synasdk-drivers-i2c-dyndmx-pinctrl \
    synasdk-drivers-phy-berlin-pcie \
    synasdk-drivers-pcie-berlin \
    synasdk-drivers-berlin-ir \
    synasdk-drivers-syna-hwmon \
    synasdk-brcm-bt-start \
    syna-trusted-app \
    pciutils \
"

IMAGE_INSTALL:append:platypus = " \
    ${PLATYPUS_DOLPHIN_INSTALL} \
    synasdk-drivers-sunplus \
"

mount_usb () {
    cat >> ${IMAGE_ROOTFS}/etc/fstab <<EOF

/dev/sda1   /media/usb1     auto     nofail,x-systemd.device-timeout=5,x-systemd.automount     0 2
/dev/sdb1   /media/usb2     auto     nofail,x-systemd.device-timeout=5,x-systemd.automount     0 2
/dev/sdc1   /media/usb3     auto     nofail,x-systemd.device-timeout=5,x-systemd.automount     0 2
/dev/sdd1   /media/usb4     auto     nofail,x-systemd.device-timeout=5,x-systemd.automount     0 2
/dev/sde1   /media/usb5     auto     nofail,x-systemd.device-timeout=5,x-systemd.automount     0 2

EOF
}

add_version () {
    echo "${ASTRA_VERSION}" > ${IMAGE_ROOTFS}/etc/astra_version
}

ROOTFS_POSTPROCESS_COMMAND += "mount_usb; add_version; "

LICENSE = "MIT"

inherit core-image

# Include dependencies and SWUpdate
inherit swupdate

SRC_URI = "file://generate_swu.sh"

do_swuimage() {
    # Change to build directory (or wherever your images are generated)
    cd ${WORKDIR}

    # Run the script to generate the .swu image and sw-description
    chmod +x generate_swu.sh
    MACHINE=${MACHINE} IMAGE_FILE="${PN}-${MACHINE}.ext4.gz" ./generate_swu.sh ${DEPLOY_DIR_IMAGE} 3

    # Copy the output .swu file to the deployment directory
    # install -d ${DEPLOY_DIR_IMAGE}
    cp ${DEPLOY_DIR_IMAGE}/image.swu ${DEPLOY_DIR_IMAGE}/astra-media-${PV}.swu
}
# Ensure swu image is built after do_image_synaimg task
addtask swuimage after do_image_synaimg
