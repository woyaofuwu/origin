SELECT COUNT(*) recordcount
  FROM tf_b_trade_svc a
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND service_id=:SERVICE_ID
   AND (modify_tag=:MODIFY_TAG OR :MODIFY_TAG = '*')
   AND exists(select 1 from TF_F_POSTINFO b
 WHERE id=TO_NUMBER(:USER_ID)
    AND a.user_id=b.id
   AND ((post_content like '%0%' AND :SERVICE_ID=138) OR (post_content like '%1%' AND :SERVICE_ID=139))
   AND post_tag='1' AND post_cyc='0'
   AND id_type='1')