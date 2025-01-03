# set SYNA_SDK_REVISION timestamp as time-epoch
# if SYNA_SDK_REVISION is not set use current time 
def astra_version_epoch(d):
    astra_version = d.getVar('SYNA_SDK_REVISION')
    if astra_version:
        import datetime
        y = int(astra_version[:4])
        m = int(astra_version[4:6])
        d = int(astra_version[6:8])
        H = int(astra_version[8:10])
        M = int(astra_version[10:12])
        epoch = datetime.datetime(y,m,d,H,M).timestamp()
    else:
        epoch = time.time()
    return '-Dtime-epoch=%d' % int(epoch)
PACKAGECONFIG[set-time-epoch] = "${@astra_version_epoch(d)},-Dtime-epoch=0"
