#!/bin/bash

# Directory setup
OUTPUT_DIR=$1
POST_SCRIPT_FILE="${OUTPUT_DIR}/post.sh"
SW_DESCRIPTION_FILE="${OUTPUT_DIR}/sw-description"
SWU_IMAGE="${OUTPUT_DIR}/image.swu"

#Mention the image file name
#set env IMAGE_FILE
#IMAGE_FILE="astra-media-sl1680.ext4.gz" ./generate_swu.sh tmp/deploy/images/sl1680 3
ROOTFS_FILE=$(basename $(readlink -f "${OUTPUT_DIR}/${IMAGE_FILE}"))

# Wait for ROOTFS_FILE
wait=0
while [ ${wait} -le 180 ]; do
    if [ ! -f ${OUTPUT_DIR}/${ROOTFS_FILE} ]; then
        sleep 5
	wait=`expr ${wait} + 5`
    else
	break
    fi
    ROOTFS_FILE=$(basename $(readlink -f "${OUTPUT_DIR}/${IMAGE_FILE}"))
done
ROOTFS_HASH=$(sha256sum "${OUTPUT_DIR}/${ROOTFS_FILE}" | awk '{ print $1 }')

cat <<'EOF' > ${POST_SCRIPT_FILE}
#!/bin/bash
rootfs=$(swupdate -g)

rootfs_num=$(echo "$rootfs" | grep -o '[0-9]*$')
if (( rootfs_num % 2 == 0 )); then
    bootctrl set-active-boot-slot 1
    fw_setenv boot_slot 2
    echo "Switching to Partition B"
else
    bootctrl set-active-boot-slot 0
    fw_setenv boot_slot 1
    echo "Switching to Partition A"
fi
EOF

POST_HASH=$(sha256sum "${POST_SCRIPT_FILE}" | awk '{ print $1 }')

# content of sw-description file (Must be modified as per needs)
cat <<EOF > ${SW_DESCRIPTION_FILE}
software =
{
    version = "1.0.0";
    description = "SWUpdate package for Astra platform";
        ${MACHINE} = {
        hardware-compatibility: [ "1.0" ];
        stable:
        {
                copy1:
                {
                        images: (
                        {
                                filename = "${ROOTFS_FILE}";
                                compressed = true;
                                device = "/dev/mmcblk0p12";
                                sha256 = "${ROOTFS_HASH}";
                        }
                        );
			scripts:(
			{
				filename = "post.sh";
				type = "postinstall";
				sha256 = "${POST_HASH}";
			}
			);
                };
                copy2:
                {
                        images: (
                        {
                                filename = "${ROOTFS_FILE}";
                                compressed = true;
                                device = "/dev/mmcblk0p13";
                                sha256 = "${ROOTFS_HASH}";
                        }
                        );
			scripts:(
			{
				filename = "post.sh";
				type = "postinstall";
				sha256 = "${POST_HASH}";
			}
			);
                }


        };
        };
}
EOF

# Create the .swu image using cpio
echo "Creating SWU image with cpio..."

(
    cd ${OUTPUT_DIR}

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
        (echo sw-description; echo sw-description.sig; echo post.sh; find . -name "${ROOTFS_FILE}" ) | cpio -o --format=newc > ${SWU_IMAGE}

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
        (echo sw-description; echo sw-description.sig; echo post.sh; find . -name "${ROOTFS_FILE}" ) | cpio -o --format=newc > ${SWU_IMAGE}

    elif [ $2 -eq 3 ]; then
        if [ ! -f mycert.cert.pem ]; then
            openssl req -x509 -newkey rsa:4096 -nodes -keyout mycert.key.pem \
                -out mycert.cert.pem -subj "/O=SWUpdate /CN=target"
        fi
        openssl cms -sign -in  sw-description -out sw-description.sig -signer mycert.cert.pem \
        -inkey mycert.key.pem -outform DER -nosmimecap -binary

	(echo sw-description; echo sw-description.sig; echo post.sh; find . -name "${ROOTFS_FILE}" ) | cpio -o --format=newc > ${SWU_IMAGE}

    else
        (echo sw-description; echo post.sh; find . -name "${ROOTFS_FILE}" ) | cpio -o --format=newc > ${SWU_IMAGE}
    fi
    if [ $? -eq 0 ]; then
        echo "SWU image created successfully: ${SWU_IMAGE}"
    else
        echo "Error creating SWU image with cpio"
        exit 1
    fi
)
