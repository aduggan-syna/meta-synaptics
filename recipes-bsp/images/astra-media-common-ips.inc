GPU_RELATED_PACKAGES = " \
    synasdk-syna-compositor \
    gstreamer1.0-plugins-syna \
"

NPU_RELATED_PACKAGES = " \
    tensorflow-lite \
    synasdk-synap-models \
    synasdk-synap-framework \
"

IMAGE_INSTALL:append:sl2615 = " \
    ${GPU_RELATED_PACKAGES} \
"

IMAGE_INSTALL:append:sl2615 = " \
    ${@bb.utils.contains('SYNA_NPU_ENABLE', '1', '${NPU_RELATED_PACKAGES}', '', d)} \
"

TOOLCHAIN_TARGET_TASK:sl2611:remove = "synasdk-synap-framework-staticdev"
TOOLCHAIN_TARGET_TASK:sl2615:remove = " \
    ${@bb.utils.contains('SYNA_NPU_ENABLE', '0', 'synasdk-synap-framework-staticdev', '', d)} \
"
