--IS_CACHE=Y
Select a.para_code1, a.para_code2
  From td_s_commpara a
 Where a.param_attr = '7640'
   and a.param_code = 'product_bind_serial_number'
   and a.subsys_code = 'CSM'
   and 1 = instr(:PHONECODE_S, a.para_code1, 1, 1)
   and a.para_code2 = :PRODUCT_ID
   and sysdate between a.start_date and a.end_date