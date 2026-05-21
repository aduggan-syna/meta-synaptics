FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:syna = " \
    file://qt-syna.patch \
"

PACKAGECONFIG:append:class-target = " gles2 icu eglfs kms gbm"
