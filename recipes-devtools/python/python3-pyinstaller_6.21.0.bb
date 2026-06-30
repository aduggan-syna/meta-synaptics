SUMMARY = "Freeze Python applications into stand-alone executables"
HOMEPAGE = "https://github.com/pyinstaller/pyinstaller"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=9026c0c1a28526b6ed7e4dce6423fbd7"

SRC_URI = "git://github.com/pyinstaller/pyinstaller;protocol=https;branch=develop"
SRCREV = "1cda34561015a90b6e1ae31dc89703799adaf13e"

S = "${WORKDIR}/git"

inherit python_hatchling

DEPENDS += " \
    python3-hatchling-native \
    python3-setuptools-native \
    python3-packaging-native \
    python3-altgraph-native \
    python3-pyinstaller-hooks-contrib-native \
"

# Work around older trove-classifiers data in the current hatchling stack.
# Remove unsupported future Python trove classifiers while keeping upstream
# metadata otherwise intact.
do_configure:append() {
    sed -i -E '/Programming Language :: Python :: 3\.(1[4-9]|[2-9][0-9])/d' ${S}/pyproject.toml
}

RDEPENDS:${PN} += " \
    python3-core \
    python3-setuptools \
    python3-packaging \
    python3-altgraph \
    python3-pyinstaller-hooks-contrib \
"

BBCLASSEXTEND = "native"
