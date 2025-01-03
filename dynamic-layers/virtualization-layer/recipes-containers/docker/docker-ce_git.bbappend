do_install:append() {
    # Ensure /etc/docker directory exists
    install -d ${D}/etc/docker

    # Create the daemon.json file directly
    cat << EOF > ${D}/etc/docker/daemon.json
    {
        "data-root": "/home/docker"
    }
EOF
}

