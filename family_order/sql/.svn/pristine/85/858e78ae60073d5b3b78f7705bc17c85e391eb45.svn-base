select c.*
  from tf_f_user a, td_s_commpara c, tf_f_user_svc b
 where a.serial_number = :SERIAL_NUMBER
   and a.remove_tag = '0'
   and b.user_Id = a.user_id
   and b.end_date > sysdate
   and c.param_attr = '3800'
   and c.param_code = 'WIDELIMIT'
   and instr(c.para_code1, '|' || b.service_id || '|') > 0