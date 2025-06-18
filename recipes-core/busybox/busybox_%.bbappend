FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
    file://devmem.cfg \
    file://0001-time-fix-max-resident-set-size-unit.patch \
    "
