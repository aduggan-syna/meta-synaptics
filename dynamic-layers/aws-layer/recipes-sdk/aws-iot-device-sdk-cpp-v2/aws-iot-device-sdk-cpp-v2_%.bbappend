PACKAGECONFIG:remove = "static"
PACKAGECONFIG[static] = ""

SRC_URI:remove = "file://001-shared-static-crt-libs.patch"

EXTRA_OECMAKE:append = " -DBUILD_SHARED_LIBS=ON"

FILES:${PN} = "${libdir}/*.so.* \
             ${libdir}/*-cpp.so*"

FILES:${PN}-dev = "${includedir} \
                  ${libdir}/libaws-c-*.so \
                  ${libdir}/libaws-checksums.so \
                  ${libdir}/libs2n.so \
                  ${libdir}/cmake \
                  ${libdir}/*/cmake \
                  ${libdir}/s2n \
                  ${libdir}/s2n/cmake"

