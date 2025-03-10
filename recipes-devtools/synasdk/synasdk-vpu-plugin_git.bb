SUMMARY = "Synaptics VPU Plugin"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

require recipes-security/optee/optee.inc

PLUGIN_UUID = "1316a183-894d-43fe-9893-bb946ae103f0"

DEPENDS = "optee-os-tadevkit optee-client"

SRC_URI = "${SYNA_SRC_OPTEE_DEV}"

SRCREV = "${SYNA_SRCREV_OPTEE_DEV}"
PV = "${ASTRA_VERSION}+git${SRCPV}"

S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee/optee_dev/ta/vpu/plugin"

do_compile() {
    oe_runmake -C ${S}
}

do_install() {
    install -d ${D}${libdir}/tee-supplicant/plugins
    install -m 0755 ${S}/${PLUGIN_UUID}.plugin ${D}${libdir}/tee-supplicant/plugins
}

FILES:${PN} = "${libdir}/tee-supplicant/plugins/"
