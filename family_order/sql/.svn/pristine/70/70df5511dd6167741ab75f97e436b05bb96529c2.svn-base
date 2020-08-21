SELECT COUNT(1) recordcount
  FROM tf_b_trade b
 WHERE b.TRADE_TYPE_CODE = :TRADE_TYPE_CODE
   AND b.user_id = TO_NUMBER(:USER_ID)
   AND rownum < 2