FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SWUPDATE_INSTALL = " \
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

IMAGE_INSTALL:append = " \
    ${SWUPDATE_INSTALL} \
"

#Only compiles if ENABLE_RESCUE_MODE = "1" in local.conf
IMAGE_INSTALL:append = "${@' astra-rescue e2fsprogs-mke2fs' if d.getVar('ENABLE_RESCUE_MODE') == '1' else ''}"
do_image[depends] += "${@bb.utils.contains('ENABLE_RESCUE_MODE', '1', 'swupdate-image:do_image_complete', '', d)}"

IMAGE_INSTALL:remove:sl1620usb = "${SWUPDATE_INSTALL}"
IMAGE_INSTALL:remove:sl1640usb = "${SWUPDATE_INSTALL}"
IMAGE_INSTALL:remove:sl1680usb = "${SWUPDATE_INSTALL}"
IMAGE_INSTALL:remove:sl2619usb = "${SWUPDATE_INSTALL}"

# Include dependencies and SWUpdate
inherit swupdate

GENERATE_SWU_SH = "generate_swu.sh"
GENERATE_SWU_SH:klamath = "generate_swu_klamath.sh"
RESCUE_OTA_SH = "rescue_ota.sh"
RESCUE_OTA_SH:klamath = "rescue_ota_klamath.sh"
RESCUE_OTA_SH:sl2611nand = "rescue_ota_klamath_nand.sh"
RESCUE_OTA_SH:sl2615nand = "rescue_ota_klamath_nand.sh"
RESCUE_OTA_SH:sl2619nand = "rescue_ota_klamath_nand.sh"

SRC_URI = "file://${GENERATE_SWU_SH} \
           file://${RESCUE_OTA_SH} \
           file://swupdate_cert_utils.sh"

do_swuimage() {
    if [ ${MACHINE} == "sl1620usb" ] || [ ${MACHINE} == "sl1640usb" ] || [ ${MACHINE} == "sl1680usb" ] || [ ${MACHINE} == "sl2619usb" ]; then
        return
    fi
    # Change to build directory (or wherever your images are generated)
    cd ${WORKDIR}

    # Run the script to generate the .swu image and sw-description
    chmod +x ${GENERATE_SWU_SH}
    MACHINE=${MACHINE} ROOTFS_IMAGE_FILE="${PN}-${MACHINE}.rootfs.ext4.gz" VERSION=${SYNA_SDK_REVISION} ./${GENERATE_SWU_SH} ${DEPLOY_DIR_IMAGE} 3

    # Copy the output .swu file to the deployment directory
    # install -d ${DEPLOY_DIR_IMAGE}
    cp ${DEPLOY_DIR_IMAGE}/image.swu ${DEPLOY_DIR_IMAGE}/astra-media.swu
}

do_rescueota() {
    rm -rf ${DEPLOY_DIR_IMAGE}/RESCUE_OTA

    if [ "${ENABLE_RESCUE_MODE}" != "1" ]; then
        exit 0
    fi

    if [ ${MACHINE} == "sl1620usb" ] || [ ${MACHINE} == "sl1640usb" ] || [ ${MACHINE} == "sl1680usb" ] || [ ${MACHINE} == "sl2619usb" ]; then
        return
    fi
    cd ${WORKDIR}

    # Run the script to generate the rescue ota image and sw-description
    chmod +x ${RESCUE_OTA_SH}
    MACHINE=${MACHINE} ROOTFS_IMAGE_FILE="${PN}-${MACHINE}.rootfs.ext4.gz" VERSION=${SYNA_SDK_REVISION} ./${RESCUE_OTA_SH} ${DEPLOY_DIR_IMAGE} 3

    cp ${DEPLOY_DIR_IMAGE}/rescue.swu ${DEPLOY_DIR_IMAGE}/RESCUE_OTA/${MACHINE}_single_copy.swu
}

# Ensure swu image is built after do_image_complete task
addtask do_swuimage after do_image_complete
addtask do_rescueota after do_swuimage before do_build
