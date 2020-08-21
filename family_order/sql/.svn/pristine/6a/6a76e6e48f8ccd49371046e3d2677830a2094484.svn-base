INSERT INTO tf_f_user_svcstate(partition_id,user_id,service_id,main_tag,state_code,start_date,end_date,update_time)
SELECT MOD(user_id,10000),user_id,service_id,main_tag,state_code,start_date,end_date,SYSDATE
  FROM tf_b_trade_svcstate_bak
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))