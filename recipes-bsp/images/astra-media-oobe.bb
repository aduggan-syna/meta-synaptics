require astra-media-common.inc

IMAGE_INSTALL:append = " \
    synasdk-oobe-resources \
    wlan-gui \
    bluetooth-gui \
    python3-pexpect \
"

IMAGE_INSTALL:append = "${@' python3-pyqt6' if d.getVar('QT_MAJOR') == '6' else ''}"
IMAGE_INSTALL:append = "${@' python3-pyqt5' if d.getVar('QT_MAJOR') == '5' else ''}"

DEV_PACKAGES = " \
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
"

IMAGE_INSTALL:append:dolphin = "${DEV_PACKAGES}"
IMAGE_INSTALL:append:klamath = " \
    ${DEV_PACKAGES} \
    python3 \
    python3-numpy \
    python3-ensurepip \
"

IMAGE_INSTALL:append:sl2619 = " \
    torq-runtime-python \
"

IMAGE_INSTALL:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'syna-weston-desktop', '', d)} \
"
