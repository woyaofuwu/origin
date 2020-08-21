INSERT INTO tf_b_trade_svc(trade_id,accept_month,user_id,service_id,serv_para1,serv_para2,serv_para3,serv_para4,serv_para5,serv_para6,serv_para7,serv_para8,modify_tag,start_date,end_date)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTRB(:TRADE_ID,5,2)),TO_NUMBER(:USER_ID),service_id,'','','','','','','','','0',TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE('20501231','yyyymmdd')
  FROM td_b_product_svc
 WHERE product_id = :PRODUCT_ID
   AND service_id = :SERVICE_ID
   AND SYSDATE BETWEEN start_date AND end_date
   AND NOT EXISTS(SELECT 1 FROM tf_f_user_svc
                   WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                     AND user_id = TO_NUMBER(:USER_ID)
                     AND service_id = :SERVICE_ID
                     AND SYSDATE < end_date)
   AND NOT EXISTS(SELECT 1 FROM tf_b_trade_svc
                   WHERE trade_id = TO_NUMBER(:TRADE_ID)
                     AND accept_month = TO_NUMBER(SUBSTRB(:TRADE_ID,5,2))
                     AND user_id = TO_NUMBER(:USER_ID)
                     AND service_id = :SERVICE_ID
                     AND modify_tag = '0'
                     AND SYSDATE < end_date)