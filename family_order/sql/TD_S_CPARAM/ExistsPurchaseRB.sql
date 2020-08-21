SELECT count(1) recordcount
  FROM tf_f_user_sale_active
 WHERE partition_id = mod(to_number(:USER_ID),10000)
   and user_id = TO_NUMBER(:USER_ID)
   AND product_id = :PURCHASE_MODE
   AND rsrv_str5 = :RSRV_STR5
   AND process_tag = '0'
   AND nvl(advance_pay,0) = 0