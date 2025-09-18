SUMMARY = "AWS IoT Managed Integrations SDK Dependences Package Group"
DESCRIPTION = "Package group to enable AWS IoT MISDK Dependencies"

LICENSE = "Apache-2.0"

PACKAGE_ARCH = "${MACHINE_ARCH}"
ALLOW_EMPTY:${PN} = "1"

# Runtime dependencies
RDEPENDS:${PN} = "\
    rapidjson \
    fmt \
    sqlite3 \
    openssl \
    cjson \
    curl \
    glib-2.0 \
    json-c \
    libnl \
    elfutils \
    libffi \
    zlib \
    libpcre \
    aws-iot-device-sdk-cpp-v2 \
    aws-iot-device-sdk-cpp-v2-samples-mqtt5-pubsub \
"

# Development dependencies (for SDK/toolchain)
RDEPENDS:${PN}-dev = "\
    aws-iot-device-sdk-cpp-v2-dev \
    nng-dev \
    nng-staticdev \
    subprocess-dev \
"
