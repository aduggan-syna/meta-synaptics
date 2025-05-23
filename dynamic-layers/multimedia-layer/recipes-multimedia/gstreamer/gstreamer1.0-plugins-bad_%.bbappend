PACKAGECONFIG:append = "fdkaac"
EXTRA_OEMESON:remove = "-Dfdkaac=disabled"

PACKAGECONFIG[fdkaac] = "-Dfdkaac=enabled,-Dfdkaac=disabled,fdk-aac"
PACKAGECONFIG:append = " kms"

FILESEXTRAPATHS:prepend := "${THISDIR}/gstreamer1.0-plugins-bad:"

SRC_URI += " \
    file://0001-av1parse-drop-codec_data-when-not-match.patch \
    file://0005-waylandsink-enable-window-drag.patch \
    file://0006-kmsink-update-atomic-buffer_rel.patch \
    file://0007-waylandsink-increase-buffer-count.patch \
    file://0008-kmssink-enable-playback-on-leasedrmfd.patch \
    file://0009-kmsink-update-dispwin-2-max-mode-size.patch \
"
EXTRA_OECONF:append = " --enable-nice"

PACKAGECONFIG:append = " webrtc srtp dash"
