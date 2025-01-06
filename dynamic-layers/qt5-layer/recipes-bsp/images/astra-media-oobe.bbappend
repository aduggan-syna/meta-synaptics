require astra-media-qt5-common.inc

IMAGE_INSTALL:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', ' synasdk-synaexplorer', '', d)} \
"
