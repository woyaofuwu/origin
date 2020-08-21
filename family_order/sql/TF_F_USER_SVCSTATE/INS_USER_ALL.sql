INSERT INTO tf_f_user_svcstate(partition_id,user_id,service_id,main_tag,state_code,start_date,end_date,
                update_time,update_staff_id,update_depart_id)
SELECT b.partition_id,b.user_id,a.service_id,a.main_tag,a.state_code,sysdate,a.end_date,
                sysdate,a.update_staff_id,a.update_depart_id
  FROM tf_b_trade_svcstate a,tf_f_user b
 WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND a.user_id = b.user_id
   AND b.remove_tag = 0
   AND a.modify_tag = '0'
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_svcstate
                    WHERE user_id = b.user_id
                      AND partition_id = b.partition_id
                      AND service_id = a.service_id
                      AND state_code = a.state_code
                      AND SYSDATE BETWEEN start_date AND end_date)