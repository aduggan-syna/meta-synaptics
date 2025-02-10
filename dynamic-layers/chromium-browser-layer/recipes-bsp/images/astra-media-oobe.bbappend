IMAGE_INSTALL:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'chromium-ozone-wayland', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'chromium-x11', '', d)} \
"
