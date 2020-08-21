INSERT INTO tf_f_user_svcstate(partition_id,user_id,service_id,main_tag,state_code,start_date,end_date,
                          update_time,update_staff_id,update_depart_id,rsrv_tag1)
SELECT mod(TO_NUMBER(user_id), 10000),a.user_id,a.service_id,a.main_tag,a.state_code,a.start_date,a.end_date,
        sysdate,a.update_staff_id,a.update_depart_id,rsrv_tag1
  FROM ucr_crm1.tf_b_trade_svcstate a
 WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND a.modify_tag = '0'
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_svcstate
                    WHERE user_id = TO_NUMBER(:USER_ID)
                      AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
                      AND service_id = a.service_id
                      AND state_code = a.state_code
                      AND end_date > start_date
                      AND end_date > a.start_date)