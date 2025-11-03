SUMMARY = "nanobind: tiny and efficient C++/Python bindings"
HOMEPAGE = "https://github.com/wjakob/nanobind"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7646f9ee25e49eaf53f89a10665c568c"

PYPI_PACKAGE = "nanobind"
SRC_URI = "https://files.pythonhosted.org/packages/source/n/${PYPI_PACKAGE}/${PYPI_PACKAGE}-${PV}.tar.gz"
SRC_URI[sha256sum] = "e7608472de99d375759814cab3e2c94aba3f9ec80e62cfef8ced495ca5c27d6e"

S = "${WORKDIR}/${PYPI_PACKAGE}-${PV}"

inherit cmake

# Don't build tests
EXTRA_OECMAKE += " -DNB_TEST=OFF -DBUILD_TESTING=OFF "

FILES:${PN} += " \
    ${prefix}/nanobind/__init__.py \
    ${prefix}/nanobind/__main__.py \
    ${prefix}/nanobind/stubgen.py \
"
FILES:${PN}-dev += " \
    ${prefix}/nanobind/cmake/* \
    ${prefix}/nanobind/include/* \
    ${prefix}/nanobind/src/* \
    ${prefix}/nanobind/ext/* \
"
