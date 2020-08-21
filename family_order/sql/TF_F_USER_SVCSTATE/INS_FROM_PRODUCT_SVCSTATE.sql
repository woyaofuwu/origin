INSERT INTO tf_f_user_svcstate(partition_id,user_id,service_id,main_tag,state_code,start_date,end_date,update_time)
SELECT DISTINCT MOD(TO_NUMBER(:USER_ID),10000),TO_NUMBER(:USER_ID),a.service_id,a.main_tag,
       b.new_state_code,SYSDATE,a.end_date,SYSDATE
 FROM td_b_product_svc a, td_s_trade_svcstate b
WHERE a.product_id=:PRODUCT_ID
  AND a.main_tag = '1'
  AND SYSDATE BETWEEN a.start_date AND a.end_date
  AND a.service_id = b.service_id
  AND b.trade_type_code = :TRADE_TYPE_CODE
  AND (a.product_id = b.product_id OR b.product_id=-1)
  AND (b.eparchy_code = :TRADE_EPARCHY_CODE OR b.eparchy_code = 'ZZZZ')
  AND NOT EXISTS (SELECT 1 FROM tf_f_user_svcstate
                   WHERE user_id = TO_NUMBER(:USER_ID)
                     AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
                     AND service_id = a.service_id
                     AND state_code = b.new_state_code
                     AND SYSDATE BETWEEN start_date AND end_date)