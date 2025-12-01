# Adding QT packages
QT_PACKAGES = " \
    qtbase qtdeclarative qtquickcontrols2 qtquickcontrols \
    qt3d qtquick3d qtmultimedia \
    synasdk-qtrenderingserver \
    qtbase-examples qtdeclarative-examples \
    synasdk-syna-settings \
"

SYNA_PLAYER_PACKAGES = " \
    synasdk-syna-player-framework synasdk-syna-video-player synasdk-syna-capability-demo synasdk-syna-ai-player \
"

IMAGE_INSTALL:append = " \
    ${QT_PACKAGES} \
    ${SYNA_PLAYER_PACKAGES} \
"

IMAGE_INSTALL:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'qtwayland', '', d)} \
"

IMAGE_INSTALL:append:dolphin = " \
    synasdk-v4l2isp-qtapp \
"

IMAGE_INSTALL:remove:sl2611 = " \
    ${QT_PACKAGES} \
    ${SYNA_PLAYER_PACKAGES} \
    qtwayland \
"
