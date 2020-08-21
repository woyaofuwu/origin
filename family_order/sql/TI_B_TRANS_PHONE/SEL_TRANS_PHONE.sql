SELECT phone_code_a,phone_code_b,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_trans_phone
 WHERE phone_code_a=:PHONE_CODE_A
AND end_date>sysdate