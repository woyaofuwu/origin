SELECT a.trade_id trade_id,a.accept_month accept_month,a.user_id user_id, a.action_code action_code,RPAD(b.action_name,40,' ') remark,a.action_count action_count,a.score_changed_sub score_changed_sub,a.value_changed_sub value_changed_sub,a.score_type_code score_type_code
FROM tf_b_trade_scoresub a,td_b_score_action b
WHERE a.trade_id=:TRADE_ID 
and a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
and (b.eparchy_code = :TRADE_EPARCHY_CODE OR b.eparchy_code='ZZZZ')
AND SYSDATE BETWEEN b.start_date AND b.end_date
AND a.action_code=b.action_code
AND b.exchange_type_code='B'