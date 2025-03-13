#!/bin/bash

# Directory setup
OUTPUT_DIR=$1
POST_SCRIPT_FILE="${OUTPUT_DIR}/post.sh"
SW_DESCRIPTION_FILE="${OUTPUT_DIR}/sw-description"
SWU_IMAGE="${OUTPUT_DIR}/image.swu"

cp $OUTPUT_DIR/SYNAIMG/fastlogo.subimg.gz $OUTPUT_DIR/SYNAIMG/firmware.subimg.gz $OUTPUT_DIR/SYNAIMG/boot.subimg.gz $OUTPUT_DIR/SYNAIMG/bl.subimg.gz $OUTPUT_DIR/SYNAIMG/tzk.subimg.gz $OUTPUT_DIR/SYNAIMG/key.subimg.gz $OUTPUT_DIR/SYNAIMG/preboot.subimg.gz $OUTPUT_DIR/

gzip -df $OUTPUT_DIR/fastlogo.subimg.gz $OUTPUT_DIR/firmware.subimg.gz $OUTPUT_DIR/boot.subimg.gz $OUTPUT_DIR/bl.subimg.gz $OUTPUT_DIR/tzk.subimg.gz $OUTPUT_DIR/key.subimg.gz $OUTPUT_DIR/preboot.subimg.gz


if [ "null${VERSION}" = "null" ]; then
    VERSION="1.6.0"
fi
#Mention the image file name (Comment the files if you don't want to include in .swu package)

ROOTFS=$(basename $(readlink -f "${OUTPUT_DIR}/${ROOTFS_IMAGE_FILE}"))

BL="bl.subimg"
BOOT="boot.subimg"
KEY="key.subimg"
TZK="tzk.subimg"
FASTLOGO="fastlogo.subimg"
FIRMWARE="firmware.subimg"
PREBOOT="preboot.subimg"

# Compute the SHA256 hash for each file
HASH_ROOTFS=$(sha256sum "$OUTPUT_DIR/$ROOTFS" | awk '{ print $1 }')
HASH_BL=$(sha256sum "$OUTPUT_DIR/$BL" | awk '{ print $1 }')
HASH_BOOT=$(sha256sum "$OUTPUT_DIR/$BOOT" | awk '{ print $1 }')
HASH_KEY=$(sha256sum "$OUTPUT_DIR/$KEY" | awk '{ print $1 }')
HASH_TZK=$(sha256sum "$OUTPUT_DIR/$TZK" | awk '{ print $1 }')
HASH_FASTLOGO=$(sha256sum "$OUTPUT_DIR/$FASTLOGO" | awk '{ print $1 }')
HASH_FIRMWARE=$(sha256sum "$OUTPUT_DIR/$FIRMWARE" | awk '{ print $1 }')
HASH_PREBOOT=$(sha256sum "$OUTPUT_DIR/$PREBOOT" | awk '{ print $1 }')

cat << 'EOF' > $POST_SCRIPT_FILE
#!/bin/bash
rootfs=$(swupdate -g)

rootfs_num=$(echo "$rootfs" | grep -o '[0-9]*$')

if (( rootfs_num % 2 == 0 )); then
    e2fsck -f /dev/mmcblk0p13
    resize2fs /dev/mmcblk0p13
    e2fsck -f /dev/mmcblk0p13
    bootctrl set-active-boot-slot 1
    fw_setenv boot_slot 2
    echo "Switching to Partition B"
else
    e2fsck -f /dev/mmcblk0p12
    resize2fs /dev/mmcblk0p12
    e2fsck -f /dev/mmcblk0p12
    bootctrl set-active-boot-slot 0
    fw_setenv boot_slot 1
    echo "Switching to Partition A"
fi

EOF

POST_HASH=$(sha256sum "${POST_SCRIPT_FILE}" | awk '{ print $1 }')

# File paths
SW_DESCRIPTION="/tmp/sw-description"
SW_VERSIONS_FILE="/etc/sw-versions"

# Check if sw-versions file exists, create if it doesn't
if [ ! -f "$SW_VERSIONS_FILE" ]; then
    touch "$SW_VERSIONS_FILE"
    echo "$SW_VERSIONS_FILE created."
fi

# Function to clean software name (removes semicolons)
clean_name() {
    local name=$1
    # Remove trailing semicolon (if it exists)
    name="${name%;}"
    echo "$name"
}

# Extract 'name' and 'version' pairs based on the PART environment variable
extract_sw_versions() {
    first_char=$(head -n 1 "/etc/env_tmp" | cut -c 1)
    if [ "$first_char" == "A" ]; then
        # Extract software names and versions only under copy1
        awk '
        # Flag to enter the "copy1" block
        /copy1:/ {in_copy1=1}
        # Exit when the closing parenthesis is reached
        /\);/ {in_copy1=0}
        # If inside the "copy1" block, process name and version
        in_copy1 {
            if ($1 == "name") {
                # Clean the name value
                name = $3;
                gsub(/"/, "", name);  # Remove quotes from name
            }
            if ($1 == "version") {
                # Clean the version value
                version = $3;
                gsub(/"/, "", version);  # Remove quotes from version
                gsub(/;$/, "", version);  # Remove trailing semicolon from version
                print name " " version;
            }
        }' "$SW_DESCRIPTION"
    elif [ "$first_char" == "B" ]; then
        # Extract software names and versions only under copy2
        awk '
        # Flag to enter the "copy2" block
        /copy2:/ {in_copy2=1}
        # Exit when the closing parenthesis is reached
        /\);/ {in_copy2=0}
        # If inside the "copy2" block, process name and version
        in_copy2 {
            if ($1 == "name") {
                # Clean the name value
                name = $3;
                gsub(/"/, "", name);  # Remove quotes from name
            }
            if ($1 == "version") {
                # Clean the version value
                version = $3;
                gsub(/"/, "", version);  # Remove quotes from version
                gsub(/;$/, "", version);  # Remove trailing semicolon from version
                print name " " version;
            }
        }' "$SW_DESCRIPTION"
    else
        echo "Error: PART variable is not set correctly. Use PART=A or PART=B."
        exit 1
    fi
}

# Function to retrieve the current version of a software from /etc/sw-versions
get_current_version() {
    local name=$1
    grep "^$name " "$SW_VERSIONS_FILE" | cut -d ' ' -f2
}

# Function to update the sw-versions file with a new version
update_version_in_sw_versions() {
    local name=$1
    local new_version=$2
    # Ensure we update only the version, not duplicate the entry
    if grep -q "^$name " "$SW_VERSIONS_FILE"; then
        # Update only if the version is newer
        local current_version=$(get_current_version "$name")
        if [[ "$(echo -e "$new_version\n$current_version" | sort -V | head -n1)" != "$new_version" ]]; then
            # Only update if the new version is newer than the current version
            sed -i "s/^$name [^ ]*/$name $new_version/" "$SW_VERSIONS_FILE"
            echo "Updated $name to version $new_version"
        else
            echo "$name version $new_version is not newer than the current version $current_version. Skipping update."
        fi
    else
        echo "$name entry not found for update."
    fi
}

# Function to add a new software entry to /etc/sw-versions
add_new_software_to_sw_versions() {
    local name=$1
    local new_version=$2
    name=$(clean_name "$name")

    # Check if the software already exists
    if grep -q "^$name " "$SW_VERSIONS_FILE"; then
        echo "$name already exists in sw-versions. No need to add it again."
    else
        # Append the new software and version to /etc/sw-versions
        echo "$name $new_version" >> "$SW_VERSIONS_FILE"
        echo "Added new software: $name with version $new_version"
    fi
}

# Extract software name and version pairs from sw-description based on PART
software_versions=$(extract_sw_versions)

# Loop through each software entry in sw-description
while IFS=" " read -r software_name sw_version; do
    # Check if software_name and sw_version are not empty
    if [[ -n "$software_name" && -n "$sw_version" ]]; then
        # Clean the software name
        software_name=$(clean_name "$software_name")

        # Get the current version from /etc/sw-versions
        current_version=$(get_current_version "$software_name")

        # If current version is empty, it means the software is not listed in /etc/sw-versions, so we add it
        if [ -z "$current_version" ]; then
            add_new_software_to_sw_versions "$software_name" "$sw_version"
        else
            # Compare versions: if the sw-version is higher, update it
            update_version_in_sw_versions "$software_name" "$sw_version"
        fi
    fi
done <<< "$software_versions"

# If sw-versions file exists, copy it to a backup location
if [ -f /etc/sw-versions ]; then
    cp /etc/sw-versions /home/sw-ver
fi

EOF


# content of sw-description file (Must be modified as per needs)
cat << EOF > $SW_DESCRIPTION_FILE
software =
{
    version = "$VERSION";
    description = "SWUpdate package for Astra platform";
        ${MACHINE} = {
        hardware-compatibility: [ "1.0" ];
        stable:
        {
                copy1:
                {
                        images: (
                        {
                                filename = "$PREBOOT";
                                device = "/dev/mmcblk0boot0";
                                sha256 = "$HASH_PREBOOT";
				name = "PREBOOT_A";
				version = "$VERSION";
				install-if-higher = true;
                        },
                        {
                                filename = "$KEY";
                                device = "/dev/mmcblk0p2";
                                sha256 = "$HASH_KEY";
				name = "KEY_A";
				version = "$VERSION";
				install-if-higher = true;

                        },
                        {
                                filename = "$TZK";
                                device = "/dev/mmcblk0p3";
                                sha256 = "$HASH_TZK";
				install-if-higher = true;
                                name = "TZKA_A";
                                version = "$VERSION";
                        },
                        {
                                filename = "$BL";
                                device = "/dev/mmcblk0p6";
                                sha256 = "$HASH_BL";
				install-if-higher = true;
                                name = "BLA_A";
                                version = "$VERSION";
                        },
			{
				filename = "$BOOT";
				device = "/dev/mmcblk0p8";
		                sha256 = "$HASH_BOOT";
				install-if-higher = true;
		                name = "boot_A";
				version = "$VERSION";
			},
                         {
                                filename = "$FIRMWARE";
                                device = "/dev/mmcblk0p10";
                                sha256 = "$HASH_FIRMWARE";
				name = "FIRMWARE_A";
				version = "$VERSION";
				install-if-higher = true;
                         },
			 {
		                filename = "$ROOTFS";
                                compressed = true;
				device = "/dev/mmcblk0p12";
		                sha256 = "$HASH_ROOTFS";
                                install-if-higher = true;
                                name = "rootfs_A";
                                version = "$VERSION";
			 },
                         {
                                filename = "$FASTLOGO";
                                device = "/dev/mmcblk0p14";
                                sha256 = "$HASH_FASTLOGO";
				name = "FASTLOGO_A";
				version = "$VERSION";
				install-if-higher = true;
                         }
			 );
			 scripts:(
			 {
				filename = "post.sh"
				type = "postinstall";
				sha256 = "${POST_HASH}";
			 }
			 );
                };
                copy2:
                {
                        images: (
                        {
                                filename = "$PREBOOT";
                                device = "/dev/mmcblk0boot1";
                                sha256 = "$HASH_PREBOOT";
                                name = "PREBOOT_B";
                                version = "$VERSION";
                                install-if-higher = true;
                        },
                        {
                                filename = "$KEY";
                                device = "/dev/mmcblk0p4";
                                sha256 = "$HASH_KEY";
                                name = "KEY_B";
                                version = "$VERSION";
                                install-if-higher = true;
                        },
                        {
                                filename = "$TZK";
                                device = "/dev/mmcblk0p5";
                                sha256 = "$HASH_TZK";
				install-if-higher = true;
                                name = "TZK_B";
                                version = "$VERSION";
                        },
                        {
                                filename = "$BL";
                                device = "/dev/mmcblk0p7";
                                sha256 = "$HASH_BL";
                                install-if-higher = true;
				name = "BL_B";
				version = "$VERSION";
                        },
                        {
                                filename = "$BOOT";
                                device = "/dev/mmcblk0p9";
                                sha256 = "$HASH_BOOT";
                                install-if-higher = true;
                                name = "boot_B";
                                version = "$VERSION";
                         },
                         {
                                filename = "$FIRMWARE";
                                device = "/dev/mmcblk0p11";
                                sha256 = "$HASH_FIRMWARE";
                                name = "FIRMWARE_B";
                                version = "$VERSION";
                                install-if-higher = true;
                         },
                         {
                                filename = "$ROOTFS";
                                compressed = true;
                                device = "/dev/mmcblk0p13";
                                sha256 = "$HASH_ROOTFS";
                                install-if-higher = true;
                                name = "rootfs_B";
                                version = "$VERSION";
                         },
                         {
                                filename = "$FASTLOGO";
                                device = "/dev/mmcblk0p15";
                                sha256 = "$HASH_FASTLOGO";
                                name = "FASTLOGO_B";
                                version = "$VERSION";
                                install-if-higher = true;
                         }
                        );
			scripts:(
			{
				filename = "post.sh"
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
    cd $OUTPUT_DIR

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
	(echo sw-description; echo sw-description.sig; echo post.sh; find . -name "$PREBOOT" -o -name "$KEY" -o -name "$TZK" -o -name "$BL" -o -name "$BOOT" -o -name "$FIRMWARE" -o -name "$ROOTFS" -o -name "$FASTLOGO") | cpio -o --format=newc > $SWU_IMAGE
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
	(echo sw-description; echo sw-description.sig; echo post.sh; find . -name "$PREBOOT" -o -name "$KEY" -o -name "$TZK" -o -name "$BL" -o -name "$BOOT" -o -name "$FIRMWARE" -o  -name "$ROOTFS" -o -name "$FASTLOGO") | cpio -o --format=newc > $SWU_IMAGE
    elif [ $2 -eq 3 ]; then
        if [ ! -f mycert.cert.pem ]; then
            openssl req -days +3650 -x509 -newkey rsa:4096 -nodes -keyout mycert.key.pem \
                -out mycert.cert.pem -subj "/O=SWUpdate /CN=target"
        fi
        openssl cms -sign -in  sw-description -out sw-description.sig -signer mycert.cert.pem \
        -inkey mycert.key.pem -outform DER -nosmimecap -binary
	#CPIO ARCHIVE (Remove or include the images in the below command as per the requirement)
	(echo sw-description; echo sw-description.sig; echo post.sh; find . -name "$PREBOOT" -o -name "$KEY" -o -name "$TZK" -o -name "$BL" -o -name "$BOOT" -o -name "$FIRMWARE" -o  -name "$ROOTFS" -o -name "$FASTLOGO") | cpio -o --format=newc > $SWU_IMAGE

    else
	#CPIO ARCHIVE (Remove or include the images in the below command as per the requirement)
	(echo sw-description; echo post.sh; find . -name "$PREBOOT" -o -name "$KEY" -o -name "$TZK" -o -name "$BL" -o -name "$BOOT" -o -name "$FIRMWARE" -o  -name "$ROOTFS" -o -name "$FASTLOGO") | cpio -o --format=newc > $SWU_IMAGE
    fi
    if [ $? -eq 0 ]; then
        echo "SWU image created successfully: ${SWU_IMAGE}"
    else
        echo "Error creating SWU image with cpio"
        exit 1
    fi
)

