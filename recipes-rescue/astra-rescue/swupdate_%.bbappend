FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://fw_env.config"

do_install:append() {
    if [ "${ENABLE_RESCUE_MODE}" = "1" ]; then
        install -Dm 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    fi
}
