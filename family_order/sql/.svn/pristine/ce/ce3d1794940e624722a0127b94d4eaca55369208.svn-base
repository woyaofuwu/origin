SELECT '1' trade_id,1 accept_month,'1' user_id,1 score_type_code,b.action_code action_code,d.action_name remark,SUM(b.action_count) action_count,to_char(sum(b.score_changed_sub)) score_changed_sub,to_char(sum(b.value_changed_sub)) value_changed_sub
FROM tf_bh_trade a,tf_b_trade_scoreSub b,td_m_depart c,td_b_score_action d
WHERE a.accept_date BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
 AND a.accept_month = to_number(substr(:START_DATE,6,2))
 AND a.trade_id=b.trade_id
 AND a.trade_type_code=:TRADE_TYPE_CODE
 AND a.cancel_tag='0'
 AND c.depart_kind_code=:DEPART_KIND_CODE
 AND (b.action_code=:ACTION_CODE OR :ACTION_CODE=-1)
 AND b.action_code=d.action_code
 AND a.trade_depart_id=c.depart_id
 GROUP BY b.action_code,d.action_name