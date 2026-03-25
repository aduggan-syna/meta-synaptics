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

cp $OUTPUT_DIR/SYNAIMG/fastlogo.subimg.gz $OUTPUT_DIR/SYNAIMG/firmware.subimg.gz $OUTPUT_DIR/SYNAIMG/boot.subimg.gz $OUTPUT_DIR/SYNAIMG/bl.subimg.gz $OUTPUT_DIR/SYNAIMG/tzk.subimg.gz $OUTPUT_DIR/SYNAIMG/key.subimg.gz $OUTPUT_DIR/SYNAIMG/preboot.subimg.gz $RESCUE_OTA/

gzip -df $RESCUE_OTA/fastlogo.subimg.gz $RESCUE_OTA/firmware.subimg.gz $RESCUE_OTA/boot.subimg.gz $RESCUE_OTA/bl.subimg.gz $RESCUE_OTA/tzk.subimg.gz $RESCUE_OTA/key.subimg.gz $RESCUE_OTA/preboot.subimg.gz


if [ "null${VERSION}" = "null" ]; then
    VERSION="1.8.0"
fi
#Mention the image file name (Comment the files if you don't want to include in .swu package)

ROOTFS=$(basename $(readlink -f "${OUTPUT_DIR}/${ROOTFS_IMAGE_FILE}"))
cp "$OUTPUT_DIR/$ROOTFS" "$RESCUE_OTA/"

BL="bl.subimg"
BOOT="boot.subimg"
KEY="key.subimg"
TZK="tzk.subimg"
FASTLOGO="fastlogo.subimg"
FIRMWARE="firmware.subimg"
PREBOOT="preboot.subimg"

# Compute the SHA256 hash for each file
HASH_ROOTFS=$(sha256sum "$RESCUE_OTA/$ROOTFS" | awk '{ print $1 }')
HASH_BL=$(sha256sum "$RESCUE_OTA/$BL" | awk '{ print $1 }')
HASH_BOOT=$(sha256sum "$RESCUE_OTA/$BOOT" | awk '{ print $1 }')
HASH_KEY=$(sha256sum "$RESCUE_OTA/$KEY" | awk '{ print $1 }')
HASH_TZK=$(sha256sum "$RESCUE_OTA/$TZK" | awk '{ print $1 }')
HASH_FASTLOGO=$(sha256sum "$RESCUE_OTA/$FASTLOGO" | awk '{ print $1 }')
HASH_FIRMWARE=$(sha256sum "$RESCUE_OTA/$FIRMWARE" | awk '{ print $1 }')
HASH_PREBOOT=$(sha256sum "$RESCUE_OTA/$PREBOOT" | awk '{ print $1 }')

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
            filename = "$PREBOOT";
            device = "/dev/mmcblk0boot0";
            sha256 = "$HASH_PREBOOT";
        },
        {
            filename = "$KEY";
            device = "/dev/mmcblk0p3";
            sha256 = "$HASH_KEY";
        },
        {
            filename = "$TZK";
            device = "/dev/mmcblk0p4";
            sha256 = "$HASH_TZK";
        },
        {
            filename = "$BL";
            device = "/dev/mmcblk0p5";
            sha256 = "$HASH_BL";
        },
        {
            filename = "$BOOT";
            device = "/dev/mmcblk0p6";
            sha256 = "$HASH_BOOT";
        },
        {
            filename = "$FIRMWARE";
            device = "/dev/mmcblk0p7";
            sha256 = "$HASH_FIRMWARE";
        },
        {
            filename = "$ROOTFS";
            compressed = true;
            device = "/dev/mmcblk0p8";
            sha256 = "$HASH_ROOTFS";
        },
        {
            filename = "$FASTLOGO";
            device = "/dev/mmcblk0p9";
            sha256 = "$HASH_FASTLOGO";
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
        (echo sw-description; echo sw-description.sig; find . -name "$PREBOOT" -o -name "$KEY" -o -name "$TZK" -o -name "$BL" -o -name "$BOOT" -o -name "$FIRMWARE" -o -name "$ROOTFS" -o -name "$FASTLOGO") | cpio -o --format=newc > $SWU_IMAGE
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
        (echo sw-description; echo sw-description.sig; find . -name "$PREBOOT" -o -name "$KEY" -o -name "$TZK" -o -name "$BL" -o -name "$BOOT" -o -name "$FIRMWARE" -o  -name "$ROOTFS" -o -name "$FASTLOGO") | cpio -o --format=newc > $SWU_IMAGE
    elif [ $2 -eq 3 ]; then
        generate_swupdate_cert || exit 1

        openssl cms -sign -in  sw-description -out sw-description.sig -signer mycert.cert.pem \
        -inkey mycert.key.pem -outform DER -nosmimecap -binary
        #CPIO ARCHIVE (Remove or include the images in the below command as per the requirement)
        (echo sw-description; echo sw-description.sig; find . -name "$PREBOOT" -o -name "$KEY" -o -name "$TZK" -o -name "$BL" -o -name "$BOOT" -o -name "$FIRMWARE" -o  -name "$ROOTFS" -o -name "$FASTLOGO") | cpio -o --format=newc > $SWU_IMAGE

    else
        #CPIO ARCHIVE (Remove or include the images in the below command as per the requirement)
        (echo sw-description; find . -name "$PREBOOT" -o -name "$KEY" -o -name "$TZK" -o -name "$BL" -o -name "$BOOT" -o -name "$FIRMWARE" -o  -name "$ROOTFS" -o -name "$FASTLOGO") | cpio -o --format=newc > $SWU_IMAGE
    fi
    if [ $? -eq 0 ]; then
        echo "SWU image created successfully: ${SWU_IMAGE}"
    else
        echo "Error creating SWU image with cpio"
        exit 1
    fi
    rm -f $ROOTFS $BL $BOOT $KEY $TZK $FASTLOGO $FIRMWARE $PREBOOT
)
