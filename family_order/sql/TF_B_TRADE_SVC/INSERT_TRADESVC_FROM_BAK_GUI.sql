INSERT INTO tf_b_trade_svc(trade_id,accept_month,user_id,service_id,serv_para1,serv_para2,serv_para3,serv_para4,serv_para5,serv_para6,serv_para7,serv_para8,modify_tag,start_date,end_date)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTRB(:TRADE_ID,5,2)),user_id,service_id,serv_para1,serv_para2,serv_para3,serv_para4,serv_para5,serv_para6,serv_para7,serv_para8,'0',SYSDATE,end_date
  FROM tf_b_trade_svc_bak
 WHERE trade_id = :OLDTRADE_ID
   AND service_id not in ('910','860')
   AND end_date > SYSDATE