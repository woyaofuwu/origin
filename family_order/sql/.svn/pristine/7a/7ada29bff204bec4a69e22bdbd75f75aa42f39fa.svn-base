SELECT count(1) recordcount
  FROM tf_b_trade_res
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND (modify_tag = :MODIFY_TAG OR :MODIFY_TAG = '*')