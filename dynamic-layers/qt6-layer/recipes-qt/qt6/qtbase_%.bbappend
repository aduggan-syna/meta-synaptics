FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

#SRC_URI:append:syna = " \
#    file://qt-syna.patch \
#    file://fix-qtbase-random-issue.patch \
#"


SRC_URI:append:syna = " \
    file://qt-syna.patch \
"
PACKAGECONFIG_GL = "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gles2', 'no-opengl', d)}"
PACKAGECONFIG:append = " icu"
PACKAGECONFIG:append:pn-qtbase = " gui opengl shader-tools widgets"
PACKAGECONFIG:append:pn-qt6-qtbase = " examples"
