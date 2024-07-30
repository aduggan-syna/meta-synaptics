DESCRIPTION = "Synaptics Mount root device partitions"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

PR = "r1"

SRC_URI = "file://syna-mount-generator"

do_install[depends] += " \
    virtual/bootloader:do_deploy \
"

do_install () {
    factory_partition=1
    home_partition=`grep home ${DEPLOY_DIR_IMAGE}/emmc_image_list|awk -F ',sd' '{print $2}'`
    install -d ${D}/${systemd_unitdir}/system-generators
    install -m 0755 ${WORKDIR}/syna-mount-generator ${D}/${systemd_unitdir}/system-generators
    sed -i -e 's,@FACTORY_PARTITION@,'${factory_partition}',g' \
           -e 's,@HOME_PARTITION@,'${home_partition}',g' \
        ${D}/${systemd_unitdir}/system-generators/syna-mount-generator
}

FILES:${PN} = "${systemd_unitdir}/system-generators/syna-mount-generator"
