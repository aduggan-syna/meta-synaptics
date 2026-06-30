SUMMARY = "Python graph (network) package"
HOMEPAGE = "https://github.com/ronaldoussoren/altgraph"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3590eb8d695bdcea3ba57e74adf8a4ed"

SRC_URI = "git://github.com/ronaldoussoren/altgraph;protocol=https;branch=master"
SRCREV = "3719ef2991e982c5d13c8ab76693a41fd8c183a3"

S = "${WORKDIR}/git"

inherit setuptools3

BBCLASSEXTEND = "native"
