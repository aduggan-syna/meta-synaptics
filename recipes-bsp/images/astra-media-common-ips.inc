GST_PACKAGES = " \
    gstreamer1.0-meta-base \
    gstreamer1.0-meta-video \
    gstreamer1.0-plugins-base-app \
    gstreamer1.0-plugins-base-rawparse \
    gstreamer1.0-plugins-base-videorate \
    gstreamer1.0-plugins-good-audioparsers \
    gstreamer1.0-plugins-good-flv \
    gstreamer1.0-plugins-good-id3demux \
    gstreamer1.0-plugins-good-imagefreeze \
    gstreamer1.0-plugins-good-isomp4 \
    gstreamer1.0-plugins-good-mpg123 \
    gstreamer1.0-plugins-good-multifile \
    gstreamer1.0-plugins-good-png \
    gstreamer1.0-plugins-good-jpeg \
    gstreamer1.0-plugins-good-video4linux2 \
    gstreamer1.0-plugins-good-udp \
    gstreamer1.0-plugins-good-rtp \
    gstreamer1.0-plugins-good-rtsp \
    gstreamer1.0-plugins-good-rtpmanager \
    gstreamer1.0-plugins-bad-autoconvert \
    gstreamer1.0-plugins-bad-camerabin \
    gstreamer1.0-plugins-bad-jpegformat \
    gstreamer1.0-plugins-bad-mpegtsdemux \
    gstreamer1.0-plugins-bad-videoparsersbad \
    gstreamer1.0-meta-debug \
    gstreamer1.0-libav \
    gstreamer1.0-rtsp-server \
"

GPU_GST_PACKAGES = " \
    gstreamer1.0-plugins-base-compositor \
    gstreamer1.0-plugins-base-opengl \
    gstreamer1.0-plugins-base-pango \
    gstreamer1.0-plugins-bad-kms \
    gstreamer1.0-plugins-good-cairo \
"

SYNA_GST_PLUGINS = "\
    gstreamer1.0-plugins-syna-videoconvertscale \
    gstreamer1.0-plugins-syna-compositor \
    gstreamer1.0-plugins-syna-synap \
    gstreamer1.0-plugins-syna-ai \
"
PACKAGES =+ "${SYNA_GST_PLUGINS} ${PN}-dev ${PN}-dbg"

GPU_RELATED_PACKAGES = " \
    synasdk-syna-compositor \
    vulkan-loader \
    synasdk-demos \
    opencv \
    glmark2 \
    ${GPU_GST_PACKAGES} \
    gstreamer1.0-plugins-syna-videoconvertscale \
    gstreamer1.0-plugins-syna-compositor \
    gstreamer1.0-plugins-syna-ai \
    gstreamer1.0-plugins-syna-synadeint2x \
"

NPU_RELATED_PACKAGES = " \
    tensorflow-lite \
    synasdk-synap-models \
    synasdk-synap-runtime \
    gstreamer1.0-plugins-syna-synap \
"

TORQ_PACKAGES = " \
    torq-kernel-module \
    torq-runtime \
"

IMAGE_INSTALL:append:sl2615 = " \
    ${GST_PACKAGES} \
    ${GPU_RELATED_PACKAGES} \
    ${@bb.utils.contains('SYNA_NPU_ENABLE', '1', '${TORQ_PACKAGES} ${NPU_RELATED_PACKAGES}', '', d)} \
"

IMAGE_INSTALL:append:sl2611 = " \
    ${GST_PACKAGES} \
"

IMAGE_INSTALL:remove:sl2611 = " \
    ${WAYLAND_PACKAGES} \
    ${XORG_MATCHBOX_DESKTOP} \
    synasdk-v4l2isp-test \
    synasdk-v4l2isp-qtapp \
"

TOOLCHAIN_TARGET_TASK:remove:sl2611 = "synasdk-synap-runtime-staticdev"
TOOLCHAIN_TARGET_TASK:remove:sl2615 = " \
    ${@bb.utils.contains('SYNA_NPU_ENABLE', '0', 'synasdk-synap-runtime-staticdev', '', d)} \
"
