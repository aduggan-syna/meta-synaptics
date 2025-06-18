FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

IMAGE_INSTALL:append = " \
    swupdate \
    swupdate-client \
    swupdate-progress \
    swupdate-tools \
    swupdate-tools-hawkbit \
    swupdate-tools-ipc \
    swu-conf \
"

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
