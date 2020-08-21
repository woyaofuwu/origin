INSERT INTO tf_f_user_svcstate(partition_id,user_id,service_id,main_tag,state_code,start_date,end_date,update_time)
SELECT TO_NUMBER(:PARTITION_ID),:USER_ID,a.service_id,b.main_tag,a.state_code,sysdate,a.end_date,SYSDATE
  FROM tf_b_trade_svcstate a,td_b_product_svc b
 WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND b.product_id= :PRODUCT_ID
   AND a.service_id = b.service_id
   AND a.modify_tag = '0'
   AND SYSDATE BETWEEN b.start_date AND b.end_date