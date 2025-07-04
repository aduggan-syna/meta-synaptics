FILESEXTRAPATHS:prepend := "${THISDIR}/gstreamer1.0-plugins-good:"

SRC_URI += " \
    file://0021-v4l2src-adding-support-for-bayer-10bit.patch;striplevel=3 \
    file://0022_v4l2transform_add_property_to_configure_crop.patch;striplevel=3 \
    file://0023-v4l2src-adding-support-yuv24.patch;striplevel=3 \
"
