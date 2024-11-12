DESCRIPTION = "Trusted Firmware-A"
LICENSE = "BSD-3-Clause & MIT"

PACKAGE_ARCH = "${MACHINE_ARCH}"
PATCHTOOL = "git"

inherit deploy

BRANCH = "master"
SRC_URI = "git://git.trustedfirmware.org/TF-A/trusted-firmware-a.git;protocol=http;branch=${BRANCH}"

# TF-A v2.7.0
SRCREV = "35f4c7295bafeb32c8bcbdfb6a3f2e74a57e732b"
LIC_FILES_CHKSUM += "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

SRC_URI:append= " \
            file://0001-genx-warm-boot-support.patch \
            file://0002-plat-syna-add-dolphin-platypus-and-myna2-support.patch \
            file://0003-berlin-pm-just-return-if-cpu-idle.patch \
        "

require recipes-devtools/synasdk/synasdk-config.inc
DEPENDS:append = " \
    synasdk-tools-native \
    synasdk-security-native \
"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

ATF_SUFFIX ??= "bin"
ATF_TARGET ?= "bl31"

ATF_PLATFORM ?= "berlin"

ATF_SOC:myna2 = "myna2"
ATF_SOC:platypus = "platypus"
ATF_SOC:dolphin = "dolphin"

do_compile() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

    oe_runmake -C ${S} BUILD_BASE=${B} DEBUG=0 CROSS_COMPILE=${TARGET_PREFIX} \
        PLAT=${ATF_PLATFORM} TARGET_SOC=${ATF_SOC} SPD=opteed ${ATF_TARGET}
}

do_install:append() {
    . ${CONFIG_FILE}
    . ${CHIP_RC_FILE}

    SYNA_KEY_PATH="${STAGING_DIR_NATIVE}/usr/share/syna/keys"
    security_keys_path="${SYNA_KEY_PATH}/${syna_chip_name}/${syna_chip_rev}"
    security_libexec_path="${STAGING_DIR_NATIVE}/usr/libexec/syna"

    in_bin=${B}/${ATF_PLATFORM}/${ATF_SOC}/release/${ATF_TARGET}.${ATF_SUFFIX}
    out_bin=${D}${nonarch_base_libdir}/firmware/tz1_en.bin
    mkdir -pv ${D}${nonarch_base_libdir}/firmware/

    prod_image_flag=0x00000000
    destination_addr=0x00120000
    ${security_libexec_path}/in_extras.py "ATF" ${B}/atf_extras.bin ${prod_image_flag} ${destination_addr}

    # Generate image
    gen_x_secure_image --chip-name=${syna_chip_name} \
                       --chip-rev=${syna_chip_rev} \
                       --img_type="ATF" \
                       --key_type="ree" \
                       --length=0x0 \
                       --extras=${B}/atf_extras.bin \
                       --workdir-security-tools=${security_libexec_path} \
                       --workdir-security-keys=${security_keys_path} \
                       --in_payload=${in_bin} \
                       --out_store=${out_bin}
}

do_deploy() {
    install -m 644 ${B}/${ATF_PLATFORM}/${ATF_SOC}/release/${ATF_TARGET}.${ATF_SUFFIX} \
        ${DEPLOYDIR}/${ATF_TARGET}.${ATF_SUFFIX}
}
addtask deploy after do_compile

FILES:${PN} = "${nonarch_base_libdir}/firmware/"
