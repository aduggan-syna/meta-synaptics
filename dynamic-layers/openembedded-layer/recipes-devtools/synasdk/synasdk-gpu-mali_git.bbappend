FILESEXTRAPATHS:append := "${THISDIR}/files:"

RDEPENDS:${PN} += " \
    opencl-icd-loader \
"

SRC_URI += " \
    file://mali.icd \
"

do_install:append() {
    mkdir -p ${D}/etc/OpenCL/vendors/
    install -m 0755 ${WORKDIR}/mali.icd ${D}/etc/OpenCL/vendors/
}

FILES:${PN} += " \
    /etc/OpenCL/vendors/mali.icd \
"
