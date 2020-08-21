SELECT to_char(user_id) user_id,trade_staff_id,term_ip,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,trade_type_code,rsrv_str1 
  FROM tl_b_identitycheck_log
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND (trade_staff_id=:TRADE_STAFF_ID or instr(lower(:EXPR),'staffid') = 0)
   AND (term_ip=:TERM_IP or instr(lower(:EXPR),'termip') = 0)
   AND accept_date>=sysdate - :NUM/24/60