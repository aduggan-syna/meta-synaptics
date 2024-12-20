#/*******************************************************************************
#*
#*             Copyright 2020, Beechwoods Software, Inc.
#*
#*******************************************************************************/

include conf/machine/include/syna-tiny.inc

# Synaptics BSP default providers
PREFERRED_PROVIDER_virtual/egl ?= "mesa"
PREFERRED_PROVIDER_virtual/libgles1 ?= "mesa"
PREFERRED_PROVIDER_virtual/libgles2 ?= "mesa"
PREFERRED_PROVIDER_virtual/mesa ?= "mesa"
PREFERRED_PROVIDER_virtual/libpvr-dri-support ?= "synasdk-gpu-pvr"
PREFERRED_PROVIDER_virtual/libgles1-pvr ?= "synasdk-gpu-pvr"
PREFERRED_PROVIDER_virtual/libgles2-pvr ?= "synasdk-gpu-pvr"
PREFERRED_PROVIDER_virtual/libpvr-scope-srv ?= "synasdk-gpu-pvr"
PREFERRED_PROVIDER_virtual/pvr-fw ?= "synasdk-gpu-pvr"
MACHINE_EXTRA_RRECOMMENDS += "synasdk-gpu-pvr"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "kernel-devicetree"
