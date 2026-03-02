python __anonymous() {
    if d.getVar("ENABLE_RESCUE_MODE") == "1":
        d.appendVar("ROOTFS_POSTPROCESS_COMMAND", " remove_swupdate_init_script;")
}

remove_swupdate_init_script () {
    rm -f ${IMAGE_ROOTFS}/etc/init.d/swupdate
}

# Add extra packages to the rootfs
IMAGE_INSTALL += "kernel-modules \
	openssh \
	openssh-sshd \
	openssh-scp \
	util-linux-lsblk \
	util-linux \
	udev \
	udev-extraconf \
	swupdate-progress \
	libubootenv \
	libubootenv-bin \
	chrony \
	rescue-support \
"

IMAGE_INSTALL:append:sl1620 = " linux-firmware-syna"

# Add cpio.gz to image formats, forcefully
IMAGE_FSTYPES:forcevariable += " cpio.gz"
