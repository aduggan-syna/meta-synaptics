#!/bin/sh

# Common gadget setup functions shared by android-gadget-setup and ncm-gadget-setup

# Detect device serial number from various sources
detect_serial() {
  if [ -f /factory_setting/serialno ]; then
    serial="$(cat /factory_setting/serialno)"
  fi

  if [ "null$serial" = "null" ]; then
    if grep -q chipid /proc/cmdline 2>/dev/null; then
      serial=$(awk -F'chipid=' '{print $2}' /proc/cmdline | awk '{print $1}')
    fi
  fi

  if [ "null$serial" = "null" ]; then
    serial=$(cat /etc/hostname 2>/dev/null)
  fi

  if [ "null$serial" = "null" ]; then
    serial="SLx6x0"
  fi

  echo "$serial"
}

# Find existing gadget or create g1
# Usage: find_or_create_gadget <gadget_name> <create_config_function>
# The create_config_function is called if no gadget exists
find_or_create_gadget() {
  local gadget_name="$1"
  local create_func="$2"

  if [ -d g1 ]; then
    # g1 already exists, use it
    cd g1 || exit 1
  else
    # g1 doesn't exist, look for any other existing gadget
    local existing_gadget=$(ls -d */ 2>/dev/null | while read dir; do
      [ -f "${dir%/}/idVendor" ] && echo "${dir%/}" && break
    done)
    
    if [ -n "$existing_gadget" ]; then
      # Found existing gadget, use it
      cd "$existing_gadget" || exit 1
      
      # Try to rename to g1 for consistency
      if [ "$existing_gadget" != "g1" ]; then
        cd ..
        mv "$existing_gadget" g1 2>/dev/null && cd g1 || cd "$existing_gadget"
      fi
    else
      # No gadget exists, create g1 using provided function
      mkdir g1
      cd g1
      $create_func
    fi
  fi

  # Ensure we're in the right directory
  if [ ! -f idVendor ]; then
    echo "Error: Could not find gadget directory" >&2
    exit 1
  fi
}

# Enable gadget on UDC if not already enabled
enable_gadget_udc() {
  local wait_time="${1:-0}"
  
  # Wait if requested (e.g., for FunctionFS mount)
  if [ "$wait_time" -gt 0 ]; then
    sleep "$wait_time"
  fi

  # Check if gadget is already enabled
  if [ -r UDC ] && [ -s UDC ]; then
    current_udc=$(cat UDC 2>/dev/null)
    if [ -n "$current_udc" ]; then
      # Already enabled
      return 0
    fi
  fi

  # Enable on available UDC
  local usb_controller=$(ls /sys/class/udc 2>/dev/null | head -n 1)
  if [ -n "$usb_controller" ]; then
    echo "$usb_controller" > UDC 2>/dev/null || {
      echo "Warning: Failed to enable gadget on UDC $usb_controller" >&2
      return 1
    }
  fi

  return 0
}

# Enable gadget on UDC with retries (for udev-triggered restart)
# Usage: enable_gadget_udc_with_retry [max_retries] [sleep_time] [check_file]
enable_gadget_udc_with_retry() {
  local max_retries="${1:-10}"
  local sleep_time="${2:-3}"
  local check_file="${3:-}"

  local usb_controller=$(ls /sys/class/udc 2>/dev/null | head -n 1)
  local udc_file="/sys/kernel/config/usb_gadget/g1/UDC"

  local i=$max_retries
  while [ $i -gt 0 ]; do
    if [ -f "$udc_file" ]; then
      local current_udc=$(cat "$udc_file" 2>/dev/null)
      
      # Check if file exists (for ADB FunctionFS)
      if [ -z "$check_file" ] || [ -f "$check_file" ]; then
        if [ "$current_udc" != "$usb_controller" ]; then
          echo "$usb_controller" > "$udc_file" 2>/dev/null
          if [ $? -eq 0 ]; then
            break
          else
            echo "Write usb_controller $usb_controller to $udc_file failed!" >&2
          fi
        else
          # Already correct
          break
        fi
      fi
    fi
    i=$((i - 1))
    sleep "$sleep_time"
  done

  return 0
}

# Cleanup gadget function
# Usage: cleanup_gadget_function <function_name_prefix>
# E.g., cleanup_gadget_function "ncm.usb0" or cleanup_gadget_function "ffs.usb0"
cleanup_gadget_function() {
  local function_name="$1"
  local prefix="${function_name%%.*}"  # Extract prefix (ncm or ffs)

  cd /sys/kernel/config/usb_gadget 2>/dev/null || exit 0

  if [ ! -d g1 ]; then
    exit 0
  fi

  cd g1 || exit 0

  # Check if other functions exist (composite gadget)
  local other_functions=$(ls -d functions/* 2>/dev/null | grep -v "$function_name" | wc -l)

  if [ "$other_functions" -gt 0 ]; then
    # Composite mode: just remove this function and its symlink
    rm -f "configs/c.1/$function_name" 2>/dev/null
    rmdir "functions/$function_name" 2>/dev/null || true
  else
    # Standalone mode: remove entire gadget
    if [ -w UDC ]; then
      echo "" > UDC 2>/dev/null || true
      sleep 1
    fi
    
    # Remove all function symlinks
    rm -f "configs/c.1/$function_name" 2>/dev/null
    
    # Remove function
    rmdir "functions/$function_name" 2>/dev/null || true
    
    # Remove config
    rmdir "configs/c.1/strings/0x409" 2>/dev/null || true
    rmdir "configs/c.1" 2>/dev/null || true
    
    # Remove strings
    rmdir "strings/0x409" 2>/dev/null || true
    
    # Remove gadget
    cd ..
    rmdir g1 2>/dev/null || true
  fi

  return 0
}

