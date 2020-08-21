UPDATE td_b_exchange_rule
  SET end_date=sysdate, 
      status='1'       
WHERE rule_id=TO_NUMBER(:RULE_ID)
AND eparchy_code=:EPARCHY_CODE