FILESEXTRAPATHS:prepend := "${THISDIR}/gstreamer1.0-plugins-good:"

SRC_URI += " \
    file://0003-v4l2object-scale-the-encoded-sizeimage-based-on-maxi.patch;striplevel=3 \
    file://0005-v4l2-Also-set-max_width-max_height-if-enum-framesize.patch;striplevel=3 \
    file://0006-v4l2-allocator-Fix-unref-log-trace-on-memory-release.patch;striplevel=3 \
    file://0007-v4l2bufferpool-queue-back-the-buffer-flagged-LAST-bu.patch;striplevel=3 \
    file://0008-v4l2bufferpool-Ensure-freshly-created-buffers-are-no.patch;striplevel=3 \
    file://0009-v4l2bufferpool-actually-queue-back-the-empty-buffer-.patch;striplevel=3 \
    file://0010-v4l2-update-v4l2-header.patch;striplevel=3 \
    file://0011-v4l2-use-the-strides-and-offsets-from-the-driver.patch;striplevel=3 \
    file://0012-v4l2-object-add-NV15-format.patch;striplevel=3 \
    file://0013-v4l2object-add-probing-of-colorspace-bt2100-pq.patch;striplevel=3 \
    file://0014-v4l2videodec-allow-variable-framerate.patch;striplevel=3 \
    file://0015-v4l2-Enable-AV1-stateful-decoder.patch;striplevel=3 \
    file://0016-v4l2videodec-src_ch-resume.patch;striplevel=3 \
    file://0017-v4l2videodec-ensure-finish-dispatchs-pending.patch;striplevel=3 \
    file://0018-v4l2-videodec-only-copy-the-frame-visual-region.patch;striplevel=3 \
    file://0019-v4l2object-parse-stride_align.patch;striplevel=3 \
    file://0020-v4l2transform-force-256-Bytes-aligned-stride.patch;striplevel=3 \
    file://0021-v4l2src-adding-support-for-bayer-10bit.patch;striplevel=3 \
    file://0022_v4l2transform_add_property_to_configure_crop.patch;striplevel=3 \
"
