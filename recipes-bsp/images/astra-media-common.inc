require astra-media-common-ips.inc

IMAGE_FEATURES:append = " debug-tweaks package-management ssh-server-dropbear"

IMAGE_FEATURES:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', ' x11-base', '', d)} \
"

TEE_TZK= " \
    tee-supplicant \
    synasdk-tee \
"

TEE_OPTEE = " \
    optee-client \
    optee-os-ta \
    optee-examples \
    optee-test \
"
XORG_XFCE = " \
    packagegroup-xfce-base \
    packagegroup-xfce-extended \
    parole \
    vlc \
    ${XORG_MINIMAL} \
"

XORG_MATCHBOX_DESKTOP = " \
    matchbox-wm \
    matchbox-panel \
    matchbox-session-sato \
    matchbox-desktop \
    matchbox-keyboard \
    matchbox-config-gtk \
    xcursor-transparent-theme adwaita-icon-theme \
    settings-daemon shutdown-desktop l3afpad \
    ${XORG_MINIMAL} \
"

XORG_MINIMAL = " \
    gstreamer1.0-plugins-base-ximagesink \
    gstreamer1.0-plugins-base-xvimagesink \
    python3-pip \
    python3-venv \
"

WAYLAND_PACKAGES = " \
    weston \
    weston-init \
    weston-examples \
    gstreamer1.0-plugins-bad-waylandsink \
"

IMAGE_INSTALL:append = " \
    kernel-modules \
    linux-firmware-syna \
"

IMAGE_INSTALL:append = " \
    gdbserver \
    curl \
    i2c-tools \
    v4l-utils \
    alsa-utils \
    vulkan-loader \
    synasdk-demos \
    synasdk-bootctrl \
    synasdk-sysctl-conf \
    synasdk-macaddr \
    syna-mount-generator \
    synasdk-syna-settings-framework \
    "

IMAGE_INSTALL:append = " \
    gstreamer1.0-meta-base \
    gstreamer1.0-meta-video \
    gstreamer1.0-plugins-base-app \
    gstreamer1.0-plugins-base-compositor \
    gstreamer1.0-plugins-base-opengl \
    gstreamer1.0-plugins-base-pango \
    gstreamer1.0-plugins-base-rawparse \
    gstreamer1.0-plugins-base-videorate \
    gstreamer1.0-plugins-good-audioparsers \
    gstreamer1.0-plugins-good-cairo \
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
    gstreamer1.0-plugins-good-rtp\
    gstreamer1.0-plugins-good-rtsp \
    gstreamer1.0-plugins-good-rtpmanager \
    gstreamer1.0-plugins-bad-autoconvert \
    gstreamer1.0-plugins-bad-camerabin \
    gstreamer1.0-plugins-bad-jpegformat \
    gstreamer1.0-plugins-bad-mpegtsdemux \
    gstreamer1.0-plugins-bad-videoparsersbad \
    gstreamer1.0-plugins-bad-kms \
    gstreamer1.0-meta-debug \
    gstreamer1.0-libav \
    gstreamer1.0-rtsp-server \
    "

IMAGE_INSTALL:append = " \
    wget \
    vim \
    wlan-connect \
    ethtool \
    syna-wifi-tools \
    hostapd \
    iptables \
    protobuf \
    opencv \
    libdrm-tests \
    "

IMAGE_INSTALL:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', ' packagegroup-core-selinux', '', d)}"

# install xfce desktop for X backend in 1680
IMAGE_INSTALL:append:dolphin = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${XORG_XFCE}', '${WAYLAND_PACKAGES}', d)} \
"
# install matchbox desktop for X backend in 1640
IMAGE_INSTALL:append:platypus = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${XORG_MATCHBOX_DESKTOP}', '${WAYLAND_PACKAGES}', d)} \
"

# install matchbox desktop for X backend in 1620
IMAGE_INSTALL:append:myna2 = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${XORG_MATCHBOX_DESKTOP}', '${WAYLAND_PACKAGES}', d)} \
"

# install matchbox desktop for X backend in 2610
IMAGE_INSTALL:append:klamath = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${XORG_MATCHBOX_DESKTOP}', '${WAYLAND_PACKAGES}', d)} \
"

IMAGE_INSTALL:append:myna2 = " \
    ${TEE_OPTEE} \
    ${GPU_RELATED_PACKAGES} \
    ${NPU_RELATED_PACKAGES} \
    brcm-patchram-plus \
    synasdk-brcm-bt-start \
"

IMAGE_INSTALL:append:klamath = " \
    ${TEE_OPTEE} \
    brcm-patchram-plus \
    synasdk-brcm-bt-start \
    synasdk-v4l2isp-test \
    synasdk-v4l2isp-qtapp \
"

PLATYPUS_DOLPHIN_INSTALL = " \
    ${TEE_OPTEE} \
    ${GPU_RELATED_PACKAGES} \
    ${NPU_RELATED_PACKAGES} \
    synasdk-brcm-bt-start \
    imgtec-pvr-firmware \
    synasdk-synap-module \
    tim-vx \
    tflite-vx-delegate \
    pciutils \
"

IMAGE_INSTALL:append:dolphin = " \
    ${PLATYPUS_DOLPHIN_INSTALL} \
    synasdk-v4l2isp-daemon \
    synasdk-v4l2isp-test \
    synasdk-v4l2isp-qtapp \
    synasdk-vpu-plugin \
"

IMAGE_INSTALL:append:platypus = " \
    ${PLATYPUS_DOLPHIN_INSTALL} \
    synasdk-vpu-plugin \
"

TOOLCHAIN_TARGET_TASK:append = " synasdk-synap-framework-staticdev kernel-devsrc"

mount_usb () {
    cat >> ${IMAGE_ROOTFS}/etc/fstab <<EOF

/dev/sda1   /media/usb1     auto     nofail,x-systemd.device-timeout=5,x-systemd.automount     0 2
/dev/sdb1   /media/usb2     auto     nofail,x-systemd.device-timeout=5,x-systemd.automount     0 2
/dev/sdc1   /media/usb3     auto     nofail,x-systemd.device-timeout=5,x-systemd.automount     0 2
/dev/sdd1   /media/usb4     auto     nofail,x-systemd.device-timeout=5,x-systemd.automount     0 2
/dev/sde1   /media/usb5     auto     nofail,x-systemd.device-timeout=5,x-systemd.automount     0 2

EOF
}

add_version () {
    echo "${ASTRA_VERSION}" > ${IMAGE_ROOTFS}/etc/astra_version
}

ROOTFS_POSTPROCESS_COMMAND += "mount_usb; add_version; "

LICENSE = "MIT"

inherit_defer ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux-image', 'core-image', d)}
