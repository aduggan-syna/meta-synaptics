FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PATCHTOOL = "git"

SRC_URI += " \
    file://astra-optee-os.patch \
"
OPTEEMACHINE = "synaptics"
SYNA_CHIP:myna2 = "myna2"
SYNA_CHIP:dolphin = "dolphin"
SYNA_CHIP:platypus = "platypus"
SYNA_CHIP:klamath = "klamath"

COMPATIBLE_MACHINE:myna2 = "sl1620"
COMPATIBLE_MACHINE:platypus = "sl1640"
COMPATIBLE_MACHINE:dolphin = "sl1680"
COMPATIBLE_MACHINE:klamath = "sl2619"

EXTRA_OEMAKE += " PLATFORM_FLAVOR=${SYNA_CHIP} "
