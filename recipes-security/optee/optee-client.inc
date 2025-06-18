SUMMARY = "OP-TEE Client API"
DESCRIPTION = "Open Portable Trusted Execution Environment - Normal World Client side of the TEE"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=69663ab153298557a59c67a60a743e5b"

inherit systemd update-rc.d cmake useradd

SRC_URI = " \
    git://github.com/OP-TEE/optee_client.git;branch=master;protocol=https \
    file://tee-supplicant.sh \
"
SRC_URI:append = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', \
    '', 'file://0001-disable-systemd-service-file.patch', d)}"

UPSTREAM_CHECK_GITTAGREGEX = "^(?P<pver>\d+(\.\d+)+)$"

S = "${WORKDIR}/git"

EXTRA_OECMAKE = " \
    -DBUILD_SHARED_LIBS=ON \
    -DCFG_TEE_FS_PARENT_PATH='${localstatedir}/lib/tee' \
    -DCFG_TEEPRIV_GROUP='teeclnt' \
"
EXTRA_OECMAKE:append:toolchain-clang = " -DCFG_WERROR=0"

do_install:append() {
    install -D -p -m0755 ${WORKDIR}/tee-supplicant.sh ${D}${sysconfdir}/init.d/tee-supplicant
    sed -i -e s:@sysconfdir@:${sysconfdir}:g \
           -e s:@sbindir@:${sbindir}:g \
              ${D}${sysconfdir}/init.d/tee-supplicant
}

SYSTEMD_SERVICE:${PN} = "tee-supplicant@.service"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "tee-supplicant"
INITSCRIPT_PARAMS:${PN} = "start 10 1 2 3 4 5 . stop 90 0 6 ."

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system teeclnt"
