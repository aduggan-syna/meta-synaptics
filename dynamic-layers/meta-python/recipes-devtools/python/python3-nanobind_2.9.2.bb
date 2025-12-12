SUMMARY = "nanobind: tiny and efficient C++/Python bindings"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7646f9ee25e49eaf53f89a10665c568c"

PYPI_PACKAGE = "nanobind"
SRC_URI = "https://files.pythonhosted.org/packages/source/n/${PYPI_PACKAGE}/${PYPI_PACKAGE}-${PV}.tar.gz"
SRC_URI[sha256sum] = "e7608472de99d375759814cab3e2c94aba3f9ec80e62cfef8ced495ca5c27d6e"

S = "${WORKDIR}/${PYPI_PACKAGE}-${PV}"
EXTRA_OECMAKE += " \
    -DNB_TEST=OFF \
    -DBUILD_TESTING=OFF \
    -DNB_USE_SUBMODULE_DEPS=ON \
"

do_install() {
    install -d ${D}${prefix}/nanobind

    for d in cmake src ext include; do
        if [ -d "${S}/${d}" ]; then
            cp -r "${S}/${d}" "${D}${prefix}/nanobind/"
        fi
    done
}

# Ship everything under /usr/nanobind
FILES:${PN} += " \
    ${prefix}/nanobind \
"
SYSROOT_DIRS += "${prefix}/nanobind"

BBCLASSEXTEND = "native nativesdk"

