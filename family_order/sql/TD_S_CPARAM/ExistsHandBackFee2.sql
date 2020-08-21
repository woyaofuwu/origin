SELECT COUNT(1) recordcount
  FROM TF_B_TRADEFEE_SUB
 WHERE TRADE_ID = to_number(:TRADE_ID)
  AND (FEE_MODE <>:FEE_MODE or FEE_TYPE_CODE <> :FEE_TYPE_CODE or FEE>0)