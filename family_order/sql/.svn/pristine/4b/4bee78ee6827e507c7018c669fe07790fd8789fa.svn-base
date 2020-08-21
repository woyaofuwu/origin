SELECT COUNT(*) recordcount
  FROM tf_b_trade_svc
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND service_id=:SERVICE_ID
   AND (modify_tag=:MODIFY_TAG OR :MODIFY_TAG = '*')
   AND (serv_para1=:SERV_PARA1 OR :SERV_PARA1 = '*')