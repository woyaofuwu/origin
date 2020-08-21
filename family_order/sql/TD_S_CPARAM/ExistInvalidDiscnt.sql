SELECT count(1) recordcount
  FROM tf_b_trade_discnt
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND discnt_code = TO_NUMBER(:DISCNT_CODE)
   AND (modify_tag = :MODIFY_TAG OR :MODIFY_TAG = '*')
   AND id = (SELECT user_id FROM tf_b_trade WHERE trade_id = TO_NUMBER(:TRADE_ID))