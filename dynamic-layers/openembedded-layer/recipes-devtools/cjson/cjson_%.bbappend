do_install:append() {
    sed -i 's;/usr/lib;${_IMPORT_PREFIX}/lib;' \
        ${D}${libdir}/cmake/cJSON/cjson*.cmake
    sed -i 's|set(_IMPORT_PREFIX "/usr")|set(_IMPORT_PREFIX "${CMAKE_CURRENT_LIST_DIR}/../../..")|' \
        ${D}${libdir}/cmake/cJSON/cjson*.cmake
}