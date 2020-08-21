--IS_CACHE=Y
SELECT rule_id,
       rule_name,
       gift_type_code,
       score,
       brand_code,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       status,
       CASE 
	   WHEN end_date < Sysdate THEN '1'
	   WHEN end_date > Sysdate THEN '0'
	   END AS RULE_STATUS
  FROM td_b_exchange_rule
 WHERE (rule_id = TO_NUMBER(:RULE_ID))