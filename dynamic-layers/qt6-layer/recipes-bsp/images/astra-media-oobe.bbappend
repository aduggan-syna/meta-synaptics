require astra-media-qt6-common.inc

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

IMAGE_INSTALL:append:sl2619 = " \
    synasdk-torq-demo \
"

IMAGE_INSTALL:append:sl2615 = " \
    ${@bb.utils.contains('SYNA_NPU_ENABLE', '1', ' synasdk-torq-demo', '', d)} \
"
