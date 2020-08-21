INSERT INTO tf_f_user_svcstate(partition_id,user_id,service_id,main_tag,state_code,start_date,end_date,update_time)
SELECT TO_NUMBER(:PARTITION_ID),:USER_ID,a.service_id,'0',a.state_code,sysdate,a.end_date,SYSDATE
  FROM tf_b_trade_svcstate a
 WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND a.modify_tag = '0'
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_svcstate
                    WHERE user_id = TO_NUMBER(:USER_ID)
                      AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
                      AND service_id = a.service_id
                      AND state_code = a.state_code
                      AND SYSDATE BETWEEN start_date AND end_date)