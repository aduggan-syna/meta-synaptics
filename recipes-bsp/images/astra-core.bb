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
    android-tools \
    android-tools-adbd \
"

IMAGE_INSTALL:append:myna2 = " \
    ${TEE_OPTEE} \
    brcm-patchram-plus \
    synasdk-brcm-bt-start \
    kernel-module-syna-dvfs \
"

PLATYPUS_DOLPHIN_INSTALL = " \
    ${TEE_OPTEE} \
    synasdk-brcm-bt-start \
    pciutils \
"

IMAGE_INSTALL:append:platypus = " \
    ${PLATYPUS_DOLPHIN_INSTALL} \
    kernel-module-sunplus \
"

IMAGE_INSTALL:append:dolphin = "${PLATYPUS_DOLPHIN_INSTALL}"
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

#Fix me
#ROOTFS_POSTPROCESS_COMMAND in android-tools bb file does not work
#Add it here to workaround it
android_tools_enable_devmode() {
    touch ${IMAGE_ROOTFS}/etc/usb-debugging-enabled
}

ROOTFS_POSTPROCESS_COMMAND += "mount_usb; add_version; android_tools_enable_devmode;"

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
    MACHINE=${MACHINE} IMAGE_FILE="${PN}-${MACHINE}.rootfs.ext4.gz" ./generate_swu.sh ${DEPLOY_DIR_IMAGE} 3

    # Copy the output .swu file to the deployment directory
    # install -d ${DEPLOY_DIR_IMAGE}
    cp ${DEPLOY_DIR_IMAGE}/image.swu ${DEPLOY_DIR_IMAGE}/astra-media-${PV}.swu
}
# Ensure swu image is built after do_image_complete task
addtask do_swuimage after do_image_complete
