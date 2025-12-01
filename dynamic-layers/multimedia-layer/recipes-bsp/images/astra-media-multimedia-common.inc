MULTIMEDIA_GPU_RELATED_PACKAGES = " \
    gstreamer1.0-plugins-bad-fdkaac \
    gstreamer1.0-plugins-base-audiomixer \
    gstreamer1.0-plugins-base-apps \
    gstreamer1.0-plugins-base-opus \
    gstreamer1.0-plugins-good-wavenc \
    gstreamer1.0-plugins-good-deinterlace \
    gstreamer1.0-plugins-ugly-a52dec \
    gstreamer1.0-plugins-bad-bayer \
    gstreamer1.0-plugins-bad-videoparsersbad \
    gstreamer1.0-plugins-bad-dash \
    gstreamer1.0-plugins-ugly-x264 \
    gstreamer1.0-plugins-bad-aom \
"

IMAGE_INSTALL:append = " \
    ${MULTIMEDIA_GPU_RELATED_PACKAGES} \
    bluealsa \
    v4l-utils \
"

IMAGE_INSTALL:remove:sl2611 = "${MULTIMEDIA_GPU_RELATED_PACKAGES}"
