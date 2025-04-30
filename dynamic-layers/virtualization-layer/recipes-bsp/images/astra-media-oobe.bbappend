VIRTULIZATION_INSTALL = " \
    docker \
    python3-docker-compose \
    connman \
    connman-client \
"

IMAGE_INSTALL:append:dolphin = "${VIRTULIZATION_INSTALL}"
IMAGE_INSTALL:append:platypus = "${VIRTULIZATION_INSTALL}"

