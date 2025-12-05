SUMMARY = "SyNAP Kernel Module"
DESCRIPTION = "${SUMMARY}"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

COMPATIBLE_MACHINE = "platypus|dolphin"

inherit module

SRC_URI = "git://github.com/synaptics-synap/runtime.git;branch=main;protocol=https;submodules=1;name=synapkernel \
          file://synap_files/0001-add-include-path-for-linux_5_15-13.patch;patchdir=${WORKDIR}/git \
          file://synap_files/0002-kernel-module-GKI-compatible-fix-14.patch;patchdir=${WORKDIR}/git \
          file://synap_files/0003-remove-ta_path-parameter-15.patch;patchdir=${WORKDIR}/git \
          file://synap_files/0004-vsi_npu_drivers-change-to-support-linux-6.12-18.patch;patchdir=${WORKDIR}/git \
          file://synap_files/0005-fix-dma_buf_release-crash-bug.patch;patchdir=${WORKDIR}/git \
          file://synap_files/0006-read_lock-before-calling-get_user_pages.patch;patchdir=${WORKDIR}/git \
          ${SYNA_SRC_SYNAP_DRIVER} \
        "

SRCREV_synapkernel = "5f7961cfb27a1e09ae77922753793840155264bb"
SRCREV_synapdriver = "${SYNA_SRCREV_SYNAP_DRIVER}"

SRCREV_FORMAT = "synapkernel_synapdriver"

PV = "${SYNAP_VERSION}+git+${@d.getVar('SRCREV_synapkernel')[:8]}"
S = "${WORKDIR}/git/kernel"

do_configure:prepend() {
    install -d ${S}/../lib/ebg_file
    install -m 0644 ${SYNA_SDK_PATH}/synap/vsi_npu_driver/lib/ebg_file/* ${S}/../lib/ebg_file/
    install -d ${S}/../lib/ta_interface
    cp -r ${SYNA_SDK_PATH}/synap/vsi_npu_driver/lib/ta_interface/* ${S}/../lib/ta_interface/
}

EXTRA_OEMAKE = "-C ${STAGING_KERNEL_DIR} M=${S}"

do_install:append() {
    rm -f ${D}/lib/modules/${KERNEL_VERSION}/extra/modules.order.*
}

RPROVIDES:${PN} += "kernel-module-synap"
KERNEL_MODULE_AUTOLOAD:append = " synap"
KERNEL_MODULE_PROBECONF:append = " synap"
module_conf_synap = "softdep synap pre: pvrsrvkm"
