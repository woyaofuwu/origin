UPDATE tf_b_pretrade
   SET ATTACH=:ATTACH
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))