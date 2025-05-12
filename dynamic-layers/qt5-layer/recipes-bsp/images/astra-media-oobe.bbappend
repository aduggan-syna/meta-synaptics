require astra-media-qt5-common.inc

IMAGE_INSTALL:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', ' synasdk-synaexplorer synasdk-syna-astra-about', '', d)} \
"

IMAGE_INSTALL:append:dolphin = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', ' synasdk-face-recognition', '', d)} \
"
