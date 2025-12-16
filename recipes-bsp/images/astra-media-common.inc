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
    synasdk-bootctrl \
    synasdk-power \
    synasdk-sysctl-conf \
    synasdk-macaddr \
    syna-mount-generator \
    synasdk-syna-settings-framework \
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
    libdrm-tests \
    opensc \
    glmark2 \
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
    synasdk-factory-ta \
    synasdk-drm-factory-ca-program \
"

IMAGE_INSTALL:append:klamath = " \
    ${TEE_OPTEE} \
    brcm-patchram-plus \
    synasdk-brcm-bt-start \
    synasdk-v4l2isp-test \
    synasdk-v4l2isp-qtapp \
"

IMAGE_INSTALL:append:sl2619 = " \
    ${TORQ_PACKAGES} \
    ${GPU_RELATED_PACKAGES} \
    ${NPU_RELATED_PACKAGES} \
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
    synasdk-factory-ta \
    synasdk-drm-factory-ca-program \
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

TOOLCHAIN_TARGET_TASK:append = " synasdk-synap-runtime-staticdev kernel-devsrc"

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
