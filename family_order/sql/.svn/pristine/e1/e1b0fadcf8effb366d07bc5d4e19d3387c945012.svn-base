SELECT phone_code_a,phone_code_b,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
FROM ti_b_trans_phone
WHERE phone_code_b=:PHONE_CODE_B
  AND end_date>sysdate