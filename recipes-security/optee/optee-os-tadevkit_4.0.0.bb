require optee-os_4.0.0.bb

SUMMARY = "OP-TEE Trusted OS TA devkit"
DESCRIPTION = "OP-TEE TA devkit for build TAs"
HOMEPAGE = "https://www.op-tee.org/"

DEPENDS += "python3-pycryptodome-native"

do_install() {
    #install TA devkit
    install -d ${D}${includedir}/optee/export-user_ta/
    for f in ${B}/export-ta_${OPTEE_ARCH}/* ; do
        cp -aR $f ${D}${includedir}/optee/export-user_ta/
    done

    #install tas in optee_armtz
    install -d ${D}${nonarch_base_libdir}/optee_armtz/
    install -m 444 ${B}/ta/*/*.ta ${D}${nonarch_base_libdir}/optee_armtz
}

do_deploy() {
	echo "Do not inherit do_deploy from optee-os."
}

#FILES:${PN} = "${includedir}/optee/"
FILES:${PN} += " ${nonarch_base_libdir}/optee_armtz/ "

# Build paths are currently embedded
INSANE_SKIP:${PN}-dev += "buildpaths"
INSANE_SKIP:append = " staticdev"
INSANE_SKIP:append = " arch"
INSANE_SKIP:append = " already-stripped"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
