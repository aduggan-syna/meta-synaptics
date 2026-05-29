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

IMAGE_INSTALL:remove:sl1620usb = "${SWUPDATE_INSTALL}"
IMAGE_INSTALL:remove:sl1640usb = "${SWUPDATE_INSTALL}"
IMAGE_INSTALL:remove:sl1680usb = "${SWUPDATE_INSTALL}"
IMAGE_INSTALL:remove:sl2619usb = "${SWUPDATE_INSTALL}"

# Include dependencies and SWUpdate
inherit swupdate

GENERATE_SWU_SH = "generate_swu.sh"
GENERATE_SWU_SH:klamath = "generate_swu_klamath.sh"

SRC_URI = "file://${GENERATE_SWU_SH} \
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
# Ensure swu image is built after do_image_complete task
addtask do_swuimage after do_image_complete
