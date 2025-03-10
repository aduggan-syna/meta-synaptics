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

SWUPDATE_INSTALL = " \
    swupdate \
    swupdate-client \
    swupdate-progress \
    swupdate-tools \
    swupdate-tools-hawkbit \
    swupdate-tools-ipc \
    libubootenv \
    libubootenv-bin \
    swu-conf \
"

IMAGE_INSTALL:append = " \
    gdbserver \
    curl \
    kernel-modules \
    linux-firmware-syna \
    i2c-tools \
    v4l-utils \
    synasdk-drivers-axi-meter \
    synasdk-drivers-bluetooth-lpm \
    synasdk-drivers-bluetooth-rfkill \
    synasdk-drivers-hl7593 \
    synasdk-drivers-rt5739 \
    synasdk-drivers-tps6286x \
    synasdk-drivers-dwc3-syna \
    synasdk-drivers-phy-syna-usb \
    synasdk-drivers-berlin-chipid \
    synasdk-drivers-rpi-panel-attiny-regulator \
    synasdk-drivers-rogue-ws \
    synasdk-demos \
    alsa-utils \
    gstreamer1.0-meta-base \
    gstreamer1.0-meta-video \
    gstreamer1.0-plugins-base-app \
    gstreamer1.0-plugins-base-pango \
    gstreamer1.0-plugins-base-opengl \
    gstreamer1.0-plugins-base-rawparse \
    gstreamer1.0-plugins-good-audioparsers \
    gstreamer1.0-plugins-good-flv \
    gstreamer1.0-plugins-good-id3demux \
    gstreamer1.0-plugins-good-imagefreeze \
    gstreamer1.0-plugins-good-isomp4 \
    gstreamer1.0-plugins-good-mpg123 \
    gstreamer1.0-plugins-good-multifile \
    gstreamer1.0-plugins-good-video4linux2 \
    gstreamer1.0-plugins-good-udp \
    gstreamer1.0-plugins-good-rtp\
    gstreamer1.0-plugins-bad-autoconvert \
    gstreamer1.0-plugins-bad-camerabin \
    gstreamer1.0-plugins-bad-jpegformat \
    gstreamer1.0-plugins-bad-mpegtsdemux \
    gstreamer1.0-plugins-bad-videoparsersbad \
    gstreamer1.0-plugins-bad-kms \
    gstreamer1.0-plugins-syna \
    gstreamer1.0-meta-debug \
    synasdk-synap-models \
    synasdk-synap-framework \
    tensorflow-lite \
    synasdk-macaddr \
    ethtool \
    gstreamer1.0-libav \
    syna-mount-generator \
    syna-wifi-tools \
    gstreamer1.0-plugins-base-compositor \
    gstreamer1.0-plugins-base-videorate \
    gstreamer1.0-plugins-good-cairo \
    gstreamer1.0-plugins-good-rtsp \
    gstreamer1.0-plugins-good-rtpmanager \
    gstreamer1.0-plugins-good-jpeg \
    gstreamer1.0-plugins-good-png \
    hostapd \
    iptables \
    protobuf \
    opencv \
    qtmultimedia \
    gstreamer1.0-rtsp-server \
    synasdk-bootctrl \
    ${SWUPDATE_INSTALL} \
    wlan-connect \
    synasdk-sysctl-conf \
    wget \
    vim \
    synasdk-syna-settings-framework \
    bridge-utils \
"

IMAGE_INSTALL:remove:sl1620usb = "${SWUPDATE_INSTALL}"
IMAGE_INSTALL:remove:sl1640usb = "${SWUPDATE_INSTALL}"
IMAGE_INSTALL:remove:sl1680usb = "${SWUPDATE_INSTALL}"

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

IMAGE_INSTALL:append:myna2 = " \
    ${TEE_OPTEE} \
    synasdk-drivers-tlc5917 \
    brcm-patchram-plus \
    synasdk-brcm-bt-start \
    synasdk-drivers-syna-dvfs \
    synasdk-drivers-syna-hwmon \
"

PLATYPUS_DOLPHIN_INSTALL = " \
    ${TEE_OPTEE} \
    synasdk-drivers-sm \
    synasdk-drivers-i2c-dyndmx-pinctrl \
    synasdk-drivers-phy-berlin-pcie \
    synasdk-drivers-pcie-berlin \
    synasdk-drivers-berlin-ir \
    synasdk-drivers-syna-hwmon \
    synasdk-brcm-bt-start \
    imgtec-pvr-firmware \
    synasdk-synap-module \
    tim-vx \
    tflite-vx-delegate \
    libdrm-tests \
    pciutils \
"

IMAGE_INSTALL:append:dolphin = " \
    ${PLATYPUS_DOLPHIN_INSTALL} \
    synasdk-v4l2isp-daemon \
    synasdk-drivers-hrx-v4l2 \
    synasdk-vpu-plugin \
    synasdk-drivers-isp \
    synasdk-v4l2isp-qtapp \
"

IMAGE_INSTALL:append:platypus = " \
    ${PLATYPUS_DOLPHIN_INSTALL} \
    synasdk-drivers-sunplus \
    synasdk-drivers-syna-dvfs \
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

inherit core-image

# Include dependencies and SWUpdate
inherit swupdate

SRC_URI = "file://generate_swu.sh"

do_swuimage() {
    if [ ${MACHINE} == "sl1620usb" ] || [ ${MACHINE} == "sl1640usb" ] || [ ${MACHINE} == "sl1680usb" ]; then
        return
    fi
    # Change to build directory (or wherever your images are generated)
    cd ${WORKDIR}

    # Run the script to generate the .swu image and sw-description
    chmod +x generate_swu.sh
    MACHINE=${MACHINE} ROOTFS_IMAGE_FILE="${PN}-${MACHINE}.ext4.gz" VERSION=${SYNA_SDK_REVISION} ./generate_swu.sh ${DEPLOY_DIR_IMAGE} 3

    # Copy the output .swu file to the deployment directory
    # install -d ${DEPLOY_DIR_IMAGE}
    cp ${DEPLOY_DIR_IMAGE}/image.swu ${DEPLOY_DIR_IMAGE}/astra-media.swu
}
# Ensure swu image is built after do_image_complete task
addtask do_swuimage after do_image_complete
