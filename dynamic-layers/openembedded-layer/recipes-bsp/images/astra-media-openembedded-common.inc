IMAGE_INSTALL:append = " \
    android-tools \
    android-tools-adbd \
    glmark2 \
    iperf2 \
    iperf3 \
    networkmanager \
    networkmanager-nmcli \
    modemmanager \
    ethtool \
    net-tools \
    tcpdump \
    pciutils \
    iputils \
    coreutils \
    rsyslog \
    gdb \
    less \
    procps \
    zip \
"

IMAGE_INSTALL:append:platypus = " \
    bluealsa \
    pulseaudio \
    evtest \
"

IMAGE_INSTALL:append:dolphin = " \
    bluealsa \
    pulseaudio \
    evtest \
"

IMAGE_INSTALL:append:myna2 = " \
    bluealsa \
    pulseaudio \
    evtest \
"
IMAGE_INSTALL:remove = " \
    ofono \
    connman \
    connman-gnome \
    connman-conf \
    packagegroup-core-tools-testapps \
"
PACKAGE_EXCLUDE:append = " \
    ofono \
    connman \
    connman-gnome \
    connman-conf \
    connman-client \
    connman-tools \
    packagegroup-core-tools-testapps \
"

PACKAGECONFIG:append:pn-networkmanager = " modemmanager ppp"
#Fix me
#ROOTFS_POSTPROCESS_COMMAND in android-tools bb file does not work
#Add it here to workaround it
android_tools_enable_devmode() {
    touch ${IMAGE_ROOTFS}/var/usb-debugging-enabled
}
ROOTFS_POSTPROCESS_COMMAND += "android_tools_enable_devmode; "

BAD_RECOMMENDATIONS += "busybox-syslog"
