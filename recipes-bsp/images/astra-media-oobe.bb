require astra-media-common.inc

IMAGE_INSTALL:append = " \
    synasdk-oobe-resources \
"
IMAGE_INSTALL:append:dolphin = " \
    python3-pip \
    gstreamer1.0-python \
    python3-venv \
    packagegroup-core-buildessential \
    cmake \
    git \
    libgomp \
    libgomp-dev \
    sqlite3 \
    python3-sqlite3 \
    wlan-gui \
"
IMAGE_INSTALL:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'syna-weston-desktop', '', d)} \
"
