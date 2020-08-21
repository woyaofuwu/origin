UPDATE TF_B_TRADE_NP
   SET cancel_tag = :CANCEL_TAG
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
   AND cancel_tag = '0'