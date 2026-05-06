PYQT_MODULES:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'QtQuick QtWidgets QtQuickWidgets', '', d)} \
"

