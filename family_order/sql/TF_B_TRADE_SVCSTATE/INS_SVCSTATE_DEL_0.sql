INSERT INTO tf_b_trade_svcstate(trade_id,accept_month,user_id,service_id,state_code,modify_tag,start_date,end_date)
SELECT :TRADE_ID,substr(:TRADE_ID, 5, 2),to_char(user_id) user_id,service_id,state_code,'1',start_date,to_date(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss')-0.00001 end_date 
  FROM tf_f_user_svcstate
 WHERE user_id = :USER_ID
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND service_id = 0
   AND state_code = '0'
   AND end_date > SYSDATE