SELECT SUM(recordnum) recordcount
   FROM
   (SELECT COUNT(*) recordnum
     FROM tf_f_user_svc a
    WHERE user_id = TO_NUMBER(:USER_ID)
      AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
      AND service_id = :SERVICE_ID
      AND end_date > SYSDATE
      AND NOT EXISTS (SELECT 1 FROM tf_b_trade_svc
                       WHERE trade_id = TO_NUMBER(:TRADE_ID)
                         AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
                         AND user_id = TO_NUMBER(:USER_ID)
                         AND decode(modify_tag, '4', '0','5','1', modify_tag) = '1'
                         AND service_id = a.service_id)
   UNION ALL
   SELECT COUNT(*) recordnum
     FROM tf_b_trade_svc b
    WHERE trade_id = TO_NUMBER(:TRADE_ID)
      AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
      AND user_id = TO_NUMBER(:USER_ID)
      AND service_id = :SERVICE_ID
      AND decode(modify_tag, '4', '0','5','1', modify_tag) in('0','U'))