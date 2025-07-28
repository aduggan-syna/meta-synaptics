# Add extra packages to the rootfs
IMAGE_INSTALL += "kernel-module-stmmac \
	kernel-module-stmmac-platform \
	kernel-module-dwmac-generic \
	kernel-module-libphy \
	kernel-module-of-mdio \
	kernel-module-phylink \
	synasdk-drivers-sunplus \
	kernel-modules \
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
	swupdate-rescue"

IMAGE_INSTALL:remove:sl1620 = "synasdk-drivers-sunplus kernel-module-libphy kernel-module-of-mdio kernel-module-phylink"

IMAGE_INSTALL:append:sl1620 = " linux-firmware-syna"

IMAGE_INSTALL:remove:sl1680 = "synasdk-drivers-sunplus kernel-module-of-mdio kernel-module-libphy"
# Add cpio.gz to image formats, forcefully
IMAGE_FSTYPES:forcevariable += " cpio.gz"

ROOTFS_POSTPROCESS_COMMAND:append = " remove_swupdate_init_script; "

remove_swupdate_init_script() {
    rm -f ${IMAGE_ROOTFS}${sysconfdir_native}/init.d/swupdate
}
