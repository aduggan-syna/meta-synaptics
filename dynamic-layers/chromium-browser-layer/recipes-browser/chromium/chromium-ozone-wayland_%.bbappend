FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://0001-linux-dri-skip-check-for-mali.patch \
"

PACKAGECONFIG:append:klamath = " mali"
PACKAGECONFIG[mali] = "is_mali=true,,synasdk-gpu-mali"
