include conf/machine/include/syna-tiny.inc

# Synaptics BSP default providers
PREFERRED_PROVIDER_virtual/libgl ?= "synasdk-gpu-mali"
PREFERRED_PROVIDER_virtual/egl ?= "synasdk-gpu-mali"
PREFERRED_PROVIDER_virtual/libgles1 ?= "synasdk-gpu-mali"
PREFERRED_PROVIDER_virtual/libgles2 ?= "synasdk-gpu-mali"
PREFERRED_PROVIDER_virtual/libgles3 ?= "synasdk-gpu-mali"
PREFERRED_PROVIDER_virtual/libgbm ?= "synasdk-gpu-mali"
MACHINE_EXTRA_RRECOMMENDS += "synasdk-gpu-mali"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "kernel-devicetree"
