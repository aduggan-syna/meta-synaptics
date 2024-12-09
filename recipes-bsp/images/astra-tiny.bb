# Do not pollute the initrd image with rootfs features
IMAGE_FEATURES = ""

LICENSE = "MIT"

inherit core-image

deltask do_rootfs do_image
