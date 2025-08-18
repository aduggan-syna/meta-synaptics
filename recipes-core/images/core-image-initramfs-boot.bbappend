PACKAGE_INSTALL:append = " \
                            kernel-module-sdhci-of-dwcmshc \
                            kernel-module-reset-berlin"

PACKAGE_INSTALL:append:myna2 = " kernel-module-gpio-regulator"

PACKAGE_INSTALL:append:platypus = " kernel-module-gpio-regulator"

ADBD_RPACKAGES = " initramfs-module-debug \
                            initramfs-module-setup-adbd \
                            kernel-module-dwc2 \
                            kernel-module-roles \
                            kernel-module-pwm-berlin \
                            synasdk-drivers-phy-syna-usb \
                            android-tools-adbd \
                            android-tools-conf-configfs"

PACKAGE_INSTALL:append:sl1640usb = " ${ADBD_RPACKAGES}"

PACKAGE_INSTALL:append:sl1680spi = " initramfs-module-debug"

PACKAGE_INSTALL:append:sl1680usb = " ${ADBD_RPACKAGES} \
                            synasdk-drivers-i2c-dyndmx-pinctrl"

PACKAGE_INSTALL:append:sl1620spi = " initramfs-module-debug"

PACKAGE_INSTALL:append:sl1640spi = " initramfs-module-debug"

PACKAGE_INSTALL:append:sl1620usb = " ${ADBD_RPACKAGES}"
