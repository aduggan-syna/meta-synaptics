genx_secure_image() {
  v_image_type=$1; shift
  in_key_type=$1; shift
  in_extras=$1; shift
  in_length=$1; shift
  f_input=$1; shift
  f_output=$1; shift

  ### Check input file ###
  [ -f $f_input ]

  ### Exectuable for generating secure image ###
  exec_cmd=gen_x_secure_image

  ### Prepare arguments ###
  unset exec_args

  # Other parameters
  exec_args="${exec_args} --chip-name=${syna_chip_name}"
  exec_args="${exec_args} --chip-rev=${syna_chip_rev}"
  exec_args="${exec_args} --img_type=$v_image_type"
  exec_args="${exec_args} --key_type=${in_key_type}"

  exec_args="${exec_args} --length=$in_length"
  exec_args="${exec_args} --extras=$in_extras"
  exec_args="${exec_args} --workdir-security-tools=${security_tools_path}"
  if [ "x${security_keys_path}" != "x" ]; then
    exec_args="${exec_args} --workdir-security-keys=${security_keys_path}"
  fi

  if [ "is${CONFIG_GENX_MCU}" = "isy" ]; then
    exec_args="${exec_args} --key_type=ree"
    exec_args="${exec_args} --tool-version=genx_v3"
  else
    exec_args="${exec_args} --tool-version=genx"
  fi

  # Input and output
  exec_args="${exec_args} --in_payload=${f_input} --out_store=${f_output}"

  ### Generate secure image ###
  eval ${exec_cmd} "${exec_args}"
}
