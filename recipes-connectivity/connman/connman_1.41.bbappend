do_install:append:dolphin() {
    ln -sf ../run/connman/resolv.conf ${D}${sysconfdir}/resolv-conf.systemd
}

