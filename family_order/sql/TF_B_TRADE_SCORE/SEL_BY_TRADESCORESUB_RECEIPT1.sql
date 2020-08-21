SELECT a.trade_id trade_id,a.accept_month accept_month,a.user_id user_id, a.rule_id action_code,a.action_count action_count,a.score_type_code score_type_code
FROM tf_b_trade_score a,TD_B_EXCHANGE_RULE b
WHERE a.trade_id=:TRADE_ID
AND a.rule_id=b.rule_id
and a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
and (b.eparchy_code = :TRADE_EPARCHY_CODE OR b.eparchy_code='ZZZZ')
AND SYSDATE BETWEEN b.start_date AND b.end_date