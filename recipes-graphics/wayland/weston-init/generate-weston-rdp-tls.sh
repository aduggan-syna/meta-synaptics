#!/bin/sh
set -eu

CRT=/etc/weston/tls.crt
KEY=/etc/weston/tls.key
# 30 days in seconds
MIN_VALID_SECONDS=$((30*24*3600))

OPENSSL=/usr/bin/openssl

mkdir -p /etc/weston
umask 077

if [ -f "$CRT" ] && [ -f "$KEY" ]; then
    # If certificate is valid for at least MIN_VALID_SECONDS, do nothing.
    if "$OPENSSL" x509 -checkend "$MIN_VALID_SECONDS" -noout -in "$CRT" >/dev/null 2>&1; then
        exit 0
    fi
    # Backup old cert/key with timestamp before regenerating
    ts=$(date -u +%Y%m%dT%H%M%SZ)
    mv "$CRT" "${CRT}.old-${ts}" || true
    mv "$KEY" "${KEY}.old-${ts}" || true
fi

echo "Generating Weston RDP TLS key and certificate"
"$OPENSSL" genrsa -out "$KEY" 2048
"$OPENSSL" req -new -x509 -key "$KEY" -out "$CRT" -days 3650 -subj "/CN=weston"
chmod 600 "$KEY" "$CRT"

exit 0
