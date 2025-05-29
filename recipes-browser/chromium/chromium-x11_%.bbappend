FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Machine-specific patches
SRC_URI:append:dolphin = " file://0001-desktop-template-for-dolphin.patch"
SRC_URI:append:platypus = " file://0001-desktop-template-for-platypus.patch"
SRC_URI:append:myna2 = " file://0001-desktop-template-for-myna2.patch"
