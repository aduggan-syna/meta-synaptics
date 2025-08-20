require astra-media-multimedia-common.inc

IMAGE_INSTALL:append = " \
    gstreamer1.0-plugins-bad-srtp \
    gstreamer1.0-plugins-bad-webrtc \
    gstreamer1.0-plugins-bad-dtls \
    gstreamer1.0-plugins-good-vpx \
    gstreamer1.0-webrtc-plugin \
    libnice \
"
