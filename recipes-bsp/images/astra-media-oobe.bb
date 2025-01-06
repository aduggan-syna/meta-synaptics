require astra-media-common.inc

IMAGE_INSTALL:append = " \
    synasdk-oobe-resources \
"
IMAGE_INSTALL:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'syna-weston-desktop', '', d)} \
"
