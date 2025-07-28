FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://rescue-network.sh"

do_install:append() {
    if [ "${ENABLE_RESCUE_MODE}" = "1" ]; then
        install -m 0755 ${WORKDIR}/rescue-network.sh ${D}${sysconfdir}/init.d/

        # Symlink into rcS.d to ensure it gets executed
        ln -sf ../init.d/rescue-network.sh ${D}${sysconfdir}/rcS.d/S99rescue-network
    fi
}
