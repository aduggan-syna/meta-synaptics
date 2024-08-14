PACKAGE_INSTALL:append = " \
                            kernel-module-sdhci-of-dwcmshc \
                            kernel-module-reset-berlin"

PACKAGE_INSTALL:append:myna2 = " kernel-module-gpio-regulator"

PACKAGE_INSTALL:append:platypus = " kernel-module-gpio-regulator"

PACKAGE_INSTALL:append:sl1640usb = " kernel-module-dwc2 \
                            kernel-module-roles \
                            kernel-module-pwm-berlin \
                            synasdk-drivers-phy-berlin-pcie \
                            synasdk-drivers-phy-syna-usb \
                            synasdk-drivers-i2c-dyndmx-pinctrl \
                            android-tools-adbd \
                            android-tools-conf-configfs"
