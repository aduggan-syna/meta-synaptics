PACKAGECONFIG:append = "fdkaac"
EXTRA_OEMESON:remove = "-Dfdkaac=disabled"

PACKAGECONFIG[fdkaac] = "-Dfdkaac=enabled,-Dfdkaac=disabled,fdk-aac"

EXTRA_OECONF:append = " --enable-nice"

PACKAGECONFIG:append = " webrtc srtp dash"
