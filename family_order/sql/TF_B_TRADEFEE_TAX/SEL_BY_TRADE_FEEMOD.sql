select to_char(TRADE_ID) TRADE_ID, ACCEPT_MONTH, to_char(USER_ID) USER_ID, FEE_MODE, FEE_TYPE_CODE, to_char(BUSI_FEE) FEE, to_char(SALE_PRICE) OLDFEE
From TF_B_TRADEFEE_TAX a
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND fee_mode = :FEE_MODE
Union All
  Select to_char(TRADE_ID) TRADE_ID, ACCEPT_MONTH, to_char(USER_ID) USER_ID, FEE_MODE, FEE_TYPE_CODE, to_char(FEE) OLDFEE, to_char(FEE) FEE
From Tf_b_Tradefee_Giftfee a
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND fee_mode = :FEE_MODE