SUMMARY = "Community maintained hooks for PyInstaller"
HOMEPAGE = "https://github.com/pyinstaller/pyinstaller-hooks-contrib"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fb584b0a12ff71c432633bf9dacb7878"

SRC_URI = "git://github.com/pyinstaller/pyinstaller-hooks-contrib;protocol=https;branch=master"
SRCREV = "791432e2a150b2282f8048df71c9ec3665aabb48"

S = "${WORKDIR}/git"

inherit setuptools3

BBCLASSEXTEND = "native"
