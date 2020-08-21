SELECT COUNT(1) recordcount
  FROM tf_b_trade_discnt
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND discnt_code = :DISCNT_CODE
   AND modify_tag = :MODIFY_TAG