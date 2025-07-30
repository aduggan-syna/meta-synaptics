IMAGE_INSTALL:append = " \
    android-tools \
    android-tools-adbd \
    glmark2 \
    iperf2 \
    iperf3 \
    ethtool \
    net-tools \
    tcpdump \
    pciutils \
    iputils \
    zip \
"

IMAGE_INSTALL:append:platypus = " \
    pulseaudio \
    evtest \
"

IMAGE_INSTALL:append:dolphin = " \
    pulseaudio \
    evtest \
"

IMAGE_INSTALL:append:myna2 = " \
    pulseaudio \
    evtest \
"

#Fix me
#ROOTFS_POSTPROCESS_COMMAND in android-tools bb file does not work
#Add it here to workaround it
android_tools_enable_devmode() {
    touch ${IMAGE_ROOTFS}/etc/usb-debugging-enabled
}
ROOTFS_POSTPROCESS_COMMAND += "android_tools_enable_devmode; "

BAD_RECOMMENDATIONS += "busybox-syslog"
