DELETE FROM td_b_exchange_rule
 WHERE rule_id=TO_NUMBER(:RULE_ID)
   AND sysdate BETWEEN start_date AND end_date