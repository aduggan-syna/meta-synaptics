LINUX_VERSION ?= "6.12.62"
PR = "r1"

require linux-syna.inc
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

S = "${WORKDIR}/git"

SRC_URI = " \
    ${SYNA_SRC_LINUX_6_12} \
    file://add-full-hid-support.cfg \
    file://devmem.cfg \
    file://iptables.cfg \
    git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.12;destsuffix=${KMETA} \
"

SRC_URI += "${SYNA_SRC_LINUX_6_12_MODULES}"

KMETA = "kernel-meta"

SRCREV_meta = "0bc72383691f29eb7fc4661afa9d67e106635929"
SRCREV_linux_main = "${SYNA_SRCREV_LINUX_6_12}"
SRCREV_linux_drivers_synaptics = "${SYNA_SRCREV_LINUX_6_12_MODULES}"

SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)}"
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'file://add-bcm-bt-driver.cfg', '', d)}"
SRC_URI += "${@bb.utils.contains('KGDB_ENABLE', '1', ' \
    file://0001-kgdb_Fix_incorrect_single_stepping_into_the_irq_handle.patch \
    file://debug_info.cfg \
    file://kgdb.cfg', '', d)}"
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'file://add-docker.cfg', '', d)}"
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'file://selinux.cfg', '', d)}"
SRC_URI:append:klamath = "file://0001-ov5647-add-flip-ctrls-and-1280x720p.patch"
SRC_URI:append:sl2619_coralboard = " file://gpio-led.cfg"

python () {
    # append defconfig if exists
    import os
    defconfig = d.getVar('THISDIR') + '/files/' + d.getVar('SYNA_KERNEL_CONFIG_FILE')
    if not os.path.exists(defconfig):
        d.setVar("KBUILD_DEFCONFIG", "${SYNA_KERNEL_CONFIG_FILE}")
        d.setVar("KCONFIG_MODE", "--alldefconfig")
    else :
        append_src_uri = d.getVar('SRC_URI') + " file://" + d.getVar('SYNA_KERNEL_CONFIG_FILE')
        d.setVar("SRC_URI", append_src_uri)

    # OpenBMC loads in kernel features via other mechanisms so this check
    # in the kernel-yocto.bbclass is not required
    d.setVar("KERNEL_DANGLING_FEATURES_WARN_ONLY","1")
}

COMPATIBLE_MACHINE = "syna"
PACKAGE_ARCH = "${MACHINE_ARCH}"
