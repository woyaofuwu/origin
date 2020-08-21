SELECT count(1) recordcount 
  FROM ucr_crm1.tf_b_trade_other A 
  WHERE A.rsrv_value_code='CREDIT_PURCHASES'
  AND A.trade_id=TO_NUMBER(:TRADE_ID)   