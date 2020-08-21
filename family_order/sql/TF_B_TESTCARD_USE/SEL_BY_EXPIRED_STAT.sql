SELECT count(serial_number) serial_number,eparchy_code,stock_id,to_char(use_limit) use_limit,card_kind_code 
  FROM tf_b_testcard_use
 WHERE eparchy_code=:EPARCHY_CODE
   AND (:STOCK_ID is null or stock_id=:STOCK_ID)
   AND (:USE_LIMIT is null or use_limit=TO_NUMBER(:USE_LIMIT))
   AND (:CARD_KIND_CODE is null or card_kind_code=:CARD_KIND_CODE)
   AND return_time_limit <= sysdate
 GROUP BY eparchy_code,stock_id,card_kind_code,use_limit