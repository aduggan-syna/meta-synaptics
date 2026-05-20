# Adding QT packages
QT_PACKAGES = " \
    qtbase qtdeclarative \
    qt3d qtquick3d qtmultimedia \
    qtbase-examples qtdeclarative-examples \
    synasdk-syna-settings \
"
#synasdk-qtrenderingserver \   removed from above

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
    synasdk-opencv-gst-od \
"

IMAGE_INSTALL:remove:sl2611 = " \
    ${QT_PACKAGES} \
    ${SYNA_PLAYER_PACKAGES} \
    qtwayland \
"
