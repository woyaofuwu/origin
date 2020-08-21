SELECT count(1) recordcount
  FROM tf_b_trade_discnt
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND discnt_code = TO_NUMBER(:DISCNT_CODE)
   AND (decode(modify_tag, '4', '0','5','1', modify_tag) = :MODIFY_TAG OR :MODIFY_TAG = '*')
   AND user_id = (SELECT user_id FROM tf_b_trade WHERE trade_id = TO_NUMBER(:TRADE_ID) AND accept_month=TO_NUMBER(:ACCEPT_MONTH))