# Adding QT packages
IMAGE_INSTALL:append = " \
    qtbase qtdeclarative qtquickcontrols2 qtquickcontrols \
    qt3d qtquick3d \
    synasdk-qtrenderingserver \
    qtbase-examples qtdeclarative-examples \
"

IMAGE_INSTALL:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', ' qtwayland synasdk-syna-player-framework synasdk-syna-video-player synasdk-syna-capability-demo synasdk-syna-ai-player', '', d)} \
"
