#/*******************************************************************************
#*
#*             Copyright 2020, Beechwoods Software, Inc.
#*
#*******************************************************************************/

SYNA_SDK_NAME = "syna"
SYNA_SDK_BUILD = "-release"
SYNA_SDK_PT_FILE = "emmc.pt"
SYNA_SDK_FLASH_ATTR = ""
SYNA_SDK_FASTLOGO_IMG = "fastlogo.subimg.gz"
DISTRO_CONFIG= "${@d.getVar('DISTRO').replace('-', '_')}"
