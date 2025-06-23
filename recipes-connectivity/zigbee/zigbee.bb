SUMMARY = "Zigbee function script"
LICENSE = "CLOSED"

SRC_URI = " \
    file://zigbee_coordinator.sh \
    file://bulb.json \
    file://switch.json \
    file://color_dimmer_switch_zc \
"

inherit systemd

RDEPENDS:${PN} += "bash"

S = "${WORKDIR}"

do_install() {
    # Create the necessary directories
    install -d ${D}${bindir}
    install -d ${D}/usr/bin

    install -m 0644 ${B}/bulb.json ${D}${bindir}/bulb.json
    install -m 0644 ${B}/switch.json ${D}${bindir}/switch.json
    install -m 0755 ${B}/zigbee_coordinator.sh ${D}${bindir}/zigbee_coordinator.sh
    install -m 0755 ${B}/color_dimmer_switch_zc ${D}${bindir}/color_dimmer_switch_zc
}

