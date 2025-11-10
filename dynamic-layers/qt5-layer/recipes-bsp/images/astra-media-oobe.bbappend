require astra-media-qt5-common.inc

IMAGE_INSTALL:append = " \
    synasdk-synaexplorer \
    synasdk-syna-astra-about \
    synasdk-syna-gst-webrtc \
    synasdk-syna-dash-player \
"

IMAGE_INSTALL:append:dolphin = " \
    nnstreamer \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', ' synasdk-face-recognition synasdk-audio-classification synasdk-superres-slideshow synasdk-superres', '', d)} \
"

IMAGE_INSTALL:append:platypus = " \
    nnstreamer \
"

IMAGE_INSTALL:append:klamath = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', ' synasdk-torq-demo', '', d)} \
"
