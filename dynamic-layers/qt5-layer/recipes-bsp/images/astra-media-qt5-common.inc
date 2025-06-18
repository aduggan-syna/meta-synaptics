# Adding QT packages
IMAGE_INSTALL:append = " \
    qtbase qtdeclarative qtquickcontrols2 qtquickcontrols \
    qt3d qtquick3d qtmultimedia \
    synasdk-qtrenderingserver \
    qtbase-examples qtdeclarative-examples \
    synasdk-syna-settings \
    gstreamer1.0-plugins-syna \
"

IMAGE_INSTALL:append = " \
    synasdk-syna-player-framework synasdk-syna-video-player synasdk-syna-capability-demo synasdk-syna-ai-player \
"

IMAGE_INSTALL:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'qtwayland', '', d)} \
"

IMAGE_INSTALL:append:dolphin = " \
    synasdk-v4l2isp-qtapp \
"
