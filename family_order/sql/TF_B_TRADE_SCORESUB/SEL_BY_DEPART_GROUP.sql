SELECT /* + index(a IDX_TF_BH_TRADE_DEPART_ID)+ */  '1' trade_id,1 accept_month,'1' user_id,1 score_type_code,b.action_code action_code,c.action_name remark,SUM(b.action_count) action_count,to_char(sum(b.score_changed_sub)) score_changed_sub,to_char(sum(b.value_changed_sub)) value_changed_sub
 FROM tf_b_trade_scoresub b,tf_bh_trade a,td_b_score_action c
WHERE a.accept_date BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
 AND a.accept_month = to_number(substr(:START_DATE,6,2))
 AND a.accept_month = b.accept_month
 AND a.trade_id=b.trade_id
 AND a.cancel_tag='0'
 AND a.trade_depart_id=:TRADE_DEPART_ID 
 AND b.action_code=c.action_code
 GROUP BY b.action_code,c.action_name