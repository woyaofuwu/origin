UPDATE tf_b_trade_svc
   SET serv_para7 = :SERV_PARA7,
       serv_para8 = DECODE(:MODE, '0', serv_para8, TO_CHAR(ADD_MONTHS(TRUNC(start_date,'mm'),TO_NUMBER(:MONTHS))-1/24/3600,'yyyy-mm-dd hh24:mi:ss'))
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id = TO_NUMBER(:USER_ID)
   AND service_id = :SERVICE_ID
   AND modify_tag = '0'