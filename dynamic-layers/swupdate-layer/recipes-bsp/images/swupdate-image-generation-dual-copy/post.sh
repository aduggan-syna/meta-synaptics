#!/bin/bash
set -e

rootfs=$(swupdate -g)

rootfs_num=$(echo "$rootfs" | grep -o '[0-9]*$')

if [ -z "$rootfs_num" ]; then
    echo "Error: could not determine current rootfs partition number" >&2
    exit 1
fi

if (( rootfs_num % 2 == 0 )); then
    e2fsck -f /dev/mmcblk0p13 > /dev/null 2>&1
    resize2fs /dev/mmcblk0p13 > /dev/null 2>&1
    e2fsck -f /dev/mmcblk0p13 > /dev/null 2>&1
    bootctrl set-active-boot-slot 1
    fw_setenv boot_slot 2
    echo "Switching to Partition B"
else
    e2fsck -f /dev/mmcblk0p12 > /dev/null 2>&1
    resize2fs /dev/mmcblk0p12 > /dev/null 2>&1
    e2fsck -f /dev/mmcblk0p12 > /dev/null 2>&1
    bootctrl set-active-boot-slot 0
    fw_setenv boot_slot 1
    echo "Switching to Partition A"
fi

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
