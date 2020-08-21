SELECT COUNT(*) recordcount from
(
  SELECT 1 FROM tf_b_trade_svc
   WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
   AND service_id = TO_NUMBER(:SERVICE_ID)
   AND modify_tag <> '1'
   AND (serv_para1 = :SERV_PARA1 OR :SERV_PARA1 = '*')
   AND end_date > sysdate
UNION
  SELECT 1 FROM tf_f_user_svc
   WHERE user_id = TO_NUMBER(:USER_ID)
     AND service_id = TO_NUMBER(:SERVICE_ID)
     AND (serv_para1 = :SERV_PARA1 OR :SERV_PARA1 = '*')
     AND end_date > sysdate
) a WHERE
    NOT EXISTS (SELECT 1 FROM tf_b_trade_svc WHERE trade_id = TO_NUMBER(:TRADE_ID)
               AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
               AND service_id = TO_NUMBER(:SERVICE_ID)
               AND modify_tag = '1'
               AND (serv_para1 = :SERV_PARA1 OR :SERV_PARA1 = '*'))
    AND NOT EXISTS
               (SELECT 1 FROM tf_b_trade_svc WHERE trade_id = TO_NUMBER(:TRADE_ID)
               AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
               AND service_id = TO_NUMBER(:SERVICE_ID)
               AND modify_tag = '2'
               AND (serv_para1 <> :SERV_PARA1 AND :SERV_PARA1 <> '*'))