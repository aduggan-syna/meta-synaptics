#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/swupdate_cert_utils.sh"

# Directory setup
OUTPUT_DIR=$1
RESCUE_OTA="${OUTPUT_DIR}/RESCUE_OTA"
mkdir -p ${RESCUE_OTA}
#POST_SCRIPT_FILE="${RESCUE_OTA}/post.sh"
SW_DESCRIPTION_FILE="${RESCUE_OTA}/sw-description"
SWU_IMAGE="${OUTPUT_DIR}/rescue.swu"

cp $OUTPUT_DIR/SYNAIMG/subimgs/pre-bootloader.subimg $OUTPUT_DIR/cmboot.subimg $OUTPUT_DIR/rootfs.subimg $RESCUE_OTA/

gzip -df $RESCUE_OTA/pre-bootloader.subimg $RESCUE_OTA/cmboot.subimg $RESCUE_OTA/rootfs.subimg

if [ "null${VERSION}" = "null" ]; then
    VERSION="1.8.0"
fi
#Mention the image file name (Comment the files if you don't want to include in .swu package)

ROOTFS=$(basename $(readlink -f "${OUTPUT_DIR}/${ROOTFS_IMAGE_FILE}"))
cp "$OUTPUT_DIR/$ROOTFS" "$RESCUE_OTA/"

PREBL="pre-bootloader.subimg"
CMBOOT="cmboot.subimg"
ROOTFS="rootfs.subimg"

# Compute the SHA256 hash for each file
HASH_ROOTFS=$(sha256sum "$RESCUE_OTA/$ROOTFS" | awk '{ print $1 }')
HASH_BL=$(sha256sum "$RESCUE_OTA/$BL" | awk '{ print $1 }')
HASH_CMBOOT=$(sha256sum "$RESCUE_OTA/$CMBOOT" | awk '{ print $1 }')

# content of sw-description file (Must be modified as per needs)
cat << EOF > $SW_DESCRIPTION_FILE
software =
{
    version = "$VERSION";
    description = "SWUpdate package for Astra platform";
        ${MACHINE} = {
        hardware-compatibility: [ "1.0" ];
        images: (
        {
            filename = "$PREBL";
            device = "/dev/mtd1";
            sha256 = "$HASH_PREBL";
        },
        {
            filename = "$CMBOOT";
            device = "/dev/mtd3";
            sha256 = "$HASH_CMBOOT";
        },
        {
            filename = "$ROOTFS";
            compressed = true;
            device = "/dev/mtd5";
            sha256 = "$HASH_ROOTFS";
        }
        );
    };
}
EOF

# Create the .swu image using cpio
echo "Creating SWU image with cpio..."

(
    cd $RESCUE_OTA

    if [ $2 -eq 1 ]; then
        if [ ! -f public.pem ]; then
            echo "test" > passout
            openssl genrsa -aes256 -passout file:passout -out priv.pem
            echo "test" > openssl rsa -in priv.pem -out public.pem -outform PEM -pubout
        fi
        if [ -f priv.pem ]; then
                echo "test" > openssl dgst -sha256 -sign priv.pem sw-description > sw-description.sig 2> error.log
                if [ $? -ne 0 ]; then
                        echo "Error signing sw-description. Check error.log for details."
                        exit 1
                fi
        else
            echo "Private key file 'priv.pem' not found."
            exit 1
        fi
        #CPIO ARCHIVE (Remove or include the images in the below command as per the requirement)
        (echo sw-description; echo sw-description.sig; find . -name "$PREBL" -o -name "$CMBOOT" -o -name "$ROOTFS") | cpio -o --format=newc > $SWU_IMAGE
    elif [ $2 -eq 2 ]; then
        if [ ! -f public.pem ]; then
            echo "test" > passout
            openssl genrsa -aes256 -passout file:passout -out priv.pem
            echo "test" > openssl rsa -in priv.pem -out public.pem -outform PEM -pubout
        fi
        if [ -f priv.pem ]; then
                echo "test" > openssl dgst -sha256 -sign priv.pem \
                -sigopt rsa_padding_mode:pss \
                -sigopt rsa_pss_saltlen:-2 \
                sw-description > sw-description.sig
                if [ $? -ne 0 ]; then
                        echo "Error signing sw-description. Check error.log for details."
                        exit 1
                fi
        else
                echo "Private key file 'priv.pem' not found."
                exit 1
        fi
        #CPIO ARCHIVE (Remove or include the images in the below command as per the requirement)
        (echo sw-description; echo sw-description.sig; find . -name "$PREBL" -o -name "$CMBOOT" -o  -name "$ROOTFS") | cpio -o --format=newc > $SWU_IMAGE
    elif [ $2 -eq 3 ]; then
        generate_swupdate_cert || exit 1

        openssl cms -sign -in  sw-description -out sw-description.sig -signer mycert.cert.pem \
        -inkey mycert.key.pem -outform DER -nosmimecap -binary
        #CPIO ARCHIVE (Remove or include the images in the below command as per the requirement)
        (echo sw-description; echo sw-description.sig; find . -name "$PREBL" -o -name "$CMBOOT" -o  -name "$ROOTFS") | cpio -o --format=newc > $SWU_IMAGE

    else
        #CPIO ARCHIVE (Remove or include the images in the below command as per the requirement)
        (echo sw-description; find . -name "$PREBL" -o -name "$CMBOOT" -o  -name "$ROOTFS") | cpio -o --format=newc > $SWU_IMAGE
    fi
    if [ $? -eq 0 ]; then
        echo "SWU image created successfully: ${SWU_IMAGE}"
    else
        echo "Error creating SWU image with cpio"
        exit 1
    fi
    rm -f $ROOTFS $PREBL $CMBOOT
)
