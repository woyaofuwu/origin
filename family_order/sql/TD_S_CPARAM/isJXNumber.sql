Select d.*
  From tf_f_user t ,tf_r_mphonecode_use d,TD_S_COMMPARA c
 where t.serial_number =:SERIAL_NUMBER
   and t.remove_tag = '0'
   and t.serial_number=d.serial_number
   and d.FEE_CODE_RULE_E=c.para_code2
   and c.param_attr='7001'
   and t.acct_tag='0'