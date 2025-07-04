PACKAGECONFIG:append = " qt"
PACKAGECONFIG[qt] = "-DWITH_QT=ON,-DWITH_QT=OFF,qtbase qttools"

EXTRA_OECMAKE:append = " \
    -DWITH_QT=ON \
    -DOE_QMAKE_PATH_EXTERNAL_HOST_BINS=${STAGING_BINDIR_NATIVE} \
"

DEPENDS:append = " qtbase qttools qttools-native qtbase-native"
RDEPENDS:${PN}:append = " qtbase qttools qttools-tools"

# Ensure qmake paths are set
OE_QMAKE_PATH_EXTERNAL_HOST_BINS = "${STAGING_BINDIR_NATIVE}"