FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
    file://devmem.cfg \
    file://brctl.cfg \
    file://0001-time-fix-max-resident-set-size-unit.patch \
    file://0001-support-chinese.patch \
    "
