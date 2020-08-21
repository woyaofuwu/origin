SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,service_id,main_tag,state_code,
         to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date  
  FROM tf_b_trade_svcstate_bak
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) 
   AND user_id = TO_NUMBER(:USER_ID)
   AND service_id = :SERVICE_ID
   AND state_code = :STATE_CODE
   AND sysdate between start_date AND end_date