INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date,id_a)
SELECT :TRADE_ID,substr(:TRADE_ID, 5, 2),to_char(user_id) user_id,'1',discnt_code,'1',
       start_date,to_date(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') end_date ,user_id_a 
  FROM tf_f_user_discnt
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date > SYSDATE