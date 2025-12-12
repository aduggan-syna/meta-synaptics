SUMMARY = "Synaptics M2M TA Test"
SECTION = "devtools"
LICENSE = "CLOSED"
LICENSE_FLAGS = "Synaptics-EULA"

DEPENDS = "optee-client synasdk-m2m-ta synasdk-tsp-ta gtest"

COMPATIBLE_MACHINE = "platypus|dolphin"

# SRC_URI = "${VSSDK_SRC_M2M_TEST}"

# m2m unit test code is located at tee/optee_dev/ta/tsp/m2m
# so we only need to  set the SRC_URI to synasdk-optee-dev
SRC_URI = "${SYNA_SRC_OPTEE_DEV}"
PV = "${ASTRA_VERSION}+git${SRCPV}"
SRCREV = "${SYNA_SRCREV_OPTEE_DEV}"

#S = "${WORKDIR}/${VSSDK_SOURCE_PREFIX}/tsp/m2m/unit_test"
S = "${WORKDIR}/${SYNA_SOURCE_PREFIX}/tee/optee_dev/ta/tsp/m2m/unit_test"
OUT = "${WORKDIR}/OUT"


do_compile() {
    oe_runmake OUT_DIR=${OUT} \
    TSP_INCLDUE_PATH=${STAGING_INCDIR}/tsp \
    M2M_INCLDUE_PATH=${STAGING_INCDIR}/m2m \
    GTEST_HEADER_PATH=${STAGING_INCDIR} \
    GTEST_LIB_PATH=${STAGING_LIBDIR}
}

do_install() {
    UNIT_TEST_BIN="m2m_test"

    install -d ${D}${bindir}
    install -m 0755 ${OUT}/${UNIT_TEST_BIN} ${D}${bindir}
}

FILES:${PN}-dev = "${bindir}/m2m_test"
