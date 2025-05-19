require astra-media-qt5-common.inc

IMAGE_INSTALL:append = " \
    synasdk-synaexplorer \
    synasdk-syna-astra-about \
"

IMAGE_INSTALL:append:dolphin = " \
    nnstreamer \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', ' synasdk-face-recognition synasdk-audio-classification synasdk-superres-slideshow', '', d)} \
"

IMAGE_INSTALL:append:platypus = " \
    nnstreamer \
"
