SELECT count(1) recordcount
  FROM tf_f_user_purchase
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND purchase_mode = :PURCHASE_MODE
   AND rsrv_str5 = :RSRV_STR5
   AND process_tag = '0'
   AND rpay_deposit = 0