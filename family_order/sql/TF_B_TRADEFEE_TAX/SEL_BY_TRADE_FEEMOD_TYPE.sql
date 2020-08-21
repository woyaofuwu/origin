SELECT to_char(trade_id) trade_id,
       FEE_MODE,
       FEE_TYPE_CODE,
       BUSI_FEE FEE,
       SALE_PRICE OLDFEE,
       FEE2,
       RATE,
       FEE1
  FROM TF_B_TRADEFEE_TAX
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND fee_mode = :FEE_MODE
   AND fee_type_code = :FEE_TYPE_CODE