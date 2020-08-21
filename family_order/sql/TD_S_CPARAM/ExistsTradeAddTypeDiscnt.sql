SELECT count(*) recordcount
  FROM tf_b_trade_discnt
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND discnt_code IN (SELECT discnt_code FROM td_b_dtype_discnt 
   WHERE discnt_type_code=:DISCNT_TYPE_CODE
   AND   end_date > SYSDATE)
   AND (modify_tag =:MODIFY_TAG OR :MODIFY_TAG = '*')