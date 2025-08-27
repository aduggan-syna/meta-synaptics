ADBD_RPACKAGES = " initramfs-module-debug \
                   initramfs-module-setup-adbd \
                   kernel-module-dwc2 \
                   kernel-module-pwm-berlin \
                   kernel-module-phy-syna-usb \
                   android-tools-adbd \
                   android-tools-conf-configfs"

PACKAGE_INSTALL:append:sl1640usb = " ${ADBD_RPACKAGES}"

PACKAGE_INSTALL:append:sl1680spi = " initramfs-module-debug"

PACKAGE_INSTALL:append:sl1680usb = " ${ADBD_RPACKAGES}"

PACKAGE_INSTALL:append:sl1620spi = " initramfs-module-debug"

PACKAGE_INSTALL:append:sl1640spi = " initramfs-module-debug"

PACKAGE_INSTALL:append:sl1620usb = " ${ADBD_RPACKAGES}"
