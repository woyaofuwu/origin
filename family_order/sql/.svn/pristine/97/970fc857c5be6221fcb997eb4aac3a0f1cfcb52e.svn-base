SELECT to_char(MIN(START_DATE)-0.00001,'yyyy-mm-dd hh24:mi:ss') START_DATE
  FROM tf_b_trade_discnt
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag=:MODIFY_TAG
   AND user_id=:USER_ID