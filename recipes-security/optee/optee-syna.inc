FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PATCHTOOL = "git"

SRC_URI += " \
    file://0001-allow-setting-sysroot-for-libgcc-lookup.patch \
    file://0002-core-Define-section-attributes-for-clang.patch \
    file://0003-optee-enable-clang-support.patch \
    file://0004-core-link-add-no-warn-rwx-segments.patch \
    file://0005-ta-pkcs11-disable-TA_FLAG_INSTANCE_KEEP_ALIVE.patch \
    file://0006-lib-extend-newlib-apis.patch \
    file://0007-plat-syna-add-dolphin-platypus-and-myna2-support.patch \
    file://0008-libutee-add-syna-syscalls.patch \
    file://0009-core-support-berlin-secure-and-non-secure-heap.patch \
"
OPTEEMACHINE = "synaptics"
SYNA_CHIP:myna2 = "myna2"
SYNA_CHIP:dolphin = "dolphin"
SYNA_CHIP:platypus= "platypus"

COMPATIBLE_MACHINE:myna2 = "sl1620"
COMPATIBLE_MACHINE:platypus = "sl1640"
COMPATIBLE_MACHINE:dolphin = "sl1680"

EXTRA_OEMAKE += " PLATFORM_FLAVOR=${SYNA_CHIP} "
