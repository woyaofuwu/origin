DELETE FROM tf_b_cardsale_log
 WHERE log_id=TO_NUMBER(:LOG_ID)
   AND sale_type_code=:SALE_TYPE_CODE