ADBD_RPACKAGES = " initramfs-module-debug \
                   initramfs-module-setup-adbd \
                   android-tools-adbd \
                   android-tools-conf-configfs"

PACKAGE_INSTALL:append:sl1640usb = " ${ADBD_RPACKAGES}"

PACKAGE_INSTALL:append:sl1680spi = " initramfs-module-debug"

PACKAGE_INSTALL:append:sl1680usb = " ${ADBD_RPACKAGES}"

PACKAGE_INSTALL:append:sl1620spi = " initramfs-module-debug"

PACKAGE_INSTALL:append:sl1640spi = " initramfs-module-debug"

PACKAGE_INSTALL:append:sl1620usb = " ${ADBD_RPACKAGES}"

PACKAGE_INSTALL:append:sl2619usb = " ${ADBD_RPACKAGES}"

# Need append 90-rootfs when setting NO_RECOMMENDATIONS 1
PACKAGE_INSTALL:append:sl2619nand = " initramfs-module-rootfs"
