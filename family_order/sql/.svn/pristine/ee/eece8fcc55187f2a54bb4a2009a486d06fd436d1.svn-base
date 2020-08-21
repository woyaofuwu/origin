INSERT INTO tf_b_trade_svc(trade_id,accept_month,user_id,service_id,serv_para1,serv_para2,serv_para3,serv_para4,serv_para5,serv_para6,serv_para7,serv_para8,modify_tag,start_date,end_date)
SELECT TO_NUMBER(:TRADE_ID),:ACCEPT_MONTH,user_id,service_id,serv_para1,serv_para2,serv_para3,serv_para4,serv_para5,serv_para6,serv_para7,serv_para8,'1',start_date,SYSDATE
  FROM tf_f_user_svc
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND service_id = 19  --国际漫游
   AND SYSDATE BETWEEN start_date AND end_date