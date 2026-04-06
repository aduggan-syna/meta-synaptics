SUMMARY = "IREE host tools (native) needed by cross builds"
LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2e982d844baa4df1c80de75470e0c5cb"

SRC_URI = " \
    git://github.com/synaptics-torq/torq-compiler.git;branch=v1.5;protocol=https;submodules=1;name=torq \
    git://github.com/synaptics-torq/iree.git;branch=torq-20240704.944;protocol=https;name=iree;submodules=1;destsuffix=git/third_party/iree \
    git://github.com/google/benchmark.git;branch=main;protocol=https;name=benchmark;submodules=1;destsuffix=git/third_party/iree/third_party/benchmark \
    git://github.com/google/googletest.git;branch=main;protocol=https;name=googletest;submodules=1;destsuffix=git/third_party/iree/third_party/googletest \
    git://github.com/dvidelabs/flatcc.git;branch=master;protocol=https;name=flatcc;submodules=1;destsuffix=git/third_party/iree/third_party/flatcc \
"
SRCREV_torq = "fae21a1acdcc4327fed2888068ae9942ce731dd7"
SRCREV_iree = "0802453ba39cc43bae75f05610e34cd35a4892e9"
SRCREV_benchmark = "1e96bb0ab5e758861f5bbbd4edbd0a8d9a2a7cae"
SRCREV_googletest = "c8393f8554419dc27b688c535b8fa4afb82146a4"
SRCREV_flatcc = "9362cd00f0007d8cbee7bff86e90fb4b6b227ff3"

SRCREV_FORMAT = "torq"
PV = "git+${@d.getVar('SRCREV_torq')[:8]}"
S = "${WORKDIR}/git"

inherit cmake pkgconfig
inherit native

OECMAKE_GENERATOR = "Unix Makefiles"
BBCLASSEXTEND = "native"

EXTRA_OECMAKE += "\
  -DCMAKE_BUILD_TYPE=RelWithDebInfo \
  -DIREE_BUILD_COMPILER=OFF \
  -DIREE_BUILD_PYTHON_BINDINGS=OFF \
  -DIREE_ENABLE_CPUINFO=OFF \
  -DCPUINFO_BUILD_BENCHMARKS=OFF \
  -DCPUINFO_BUILD_TOOLS=OFF \
  -DBENCHMARK_ENABLE_GTEST_TESTS=OFF \
"

OECMAKE_TARGET_COMPILE = "generate_embed_data iree-flatcc-cli"
IREE_TOOLS_DIR = "${B}/third_party/iree/tools"

do_install() {
    install -d ${D}${bindir}/iree
    install -m 0755 ${IREE_TOOLS_DIR}/generate_embed_data ${D}${bindir}/iree/
    install -m 0755 ${IREE_TOOLS_DIR}/iree-flatcc-cli ${D}${bindir}/iree/              
}
PACKAGES = ""

