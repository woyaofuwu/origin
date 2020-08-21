DELETE FROM tf_b_pretrade where 1=1
 and trade_id=TO_NUMBER(:TRADE_ID)
 and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))