# Configuring the features required in each module of QT Packages
PACKAGECONFIG:append:class-target = ' gles2 sm gif sql-sqlite pcre '
PACKAGECONFIG:append:class-target = ' glib fontconfig linuxfb kms gbm xkbcommon eglfs '
PACKAGECONFIG_DEFAULT:remove = ' tests vulkan '
PACKAGECONFIG:append:class-target = " examples"
