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

GENERATE_SWU_SH = "generate_swu.sh"
GENERATE_SWU_SH:klamath = "generate_swu_klamath.sh"

SRC_URI = "file://${GENERATE_SWU_SH} \
           file://swupdate_cert_utils.sh"

do_swuimage() {
    # Change to build directory (or wherever your images are generated)
    cd ${WORKDIR}

    # Run the script to generate the .swu image and sw-description
    chmod +x ${GENERATE_SWU_SH} 
    MACHINE=${MACHINE} IMAGE_FILE="${PN}-${MACHINE}.rootfs.ext4.gz" ./${GENERATE_SWU_SH} ${DEPLOY_DIR_IMAGE} 3

    # Copy the output .swu file to the deployment directory
    # install -d ${DEPLOY_DIR_IMAGE}
    cp ${DEPLOY_DIR_IMAGE}/image.swu ${DEPLOY_DIR_IMAGE}/astra-media-${PV}.swu
}
# Ensure swu image is built after do_image_complete task
addtask do_swuimage after do_image_complete
