INSERT INTO tf_b_trade_svc(trade_id,accept_month,user_id,service_id,serv_para1,serv_para2,serv_para3,serv_para4,serv_para5,serv_para6,serv_para7,serv_para8,modify_tag,start_date,end_date)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTR(:TRADE_ID,5,2)),user_id,service_id,serv_para1,serv_para2,serv_para3,serv_para4,serv_para5,serv_para6,serv_para7,serv_para8,'0',start_date,TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss')
  FROM tf_f_user_svc a
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date > SYSDATE
   AND end_date > start_date
   AND NOT EXISTS (SELECT 1 FROM tf_b_trade_svc
                    WHERE trade_id = TO_NUMBER(:TRADE_ID)
                      AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                      AND user_id = a.user_id
                      AND service_id = a.service_id)