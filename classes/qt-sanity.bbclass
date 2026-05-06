python __anonymous() {
    qt = d.getVar("QT_MAJOR")
    bbmask = d.getVar("BBMASK") or ""

    if qt == "6" and "meta-qt6" in bbmask:
        bb.fatal("QT_VERSION=6 but meta-qt6 is masked")

    if qt == "5" and "meta-qt5" in bbmask:
        bb.fatal("QT_VERSION=5 but meta-qt5 is masked")
}
