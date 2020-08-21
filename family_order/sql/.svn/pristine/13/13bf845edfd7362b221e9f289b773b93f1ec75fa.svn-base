SELECT TO_CHAR(b.trade_id) trade_id,TO_CHAR(a.accept_date,'YYYY-MM-DD HH24:MI:SS') accept_date,TO_CHAR(score_changed) score_changed,a.trade_staff_id,a.trade_depart_id,a.cancel_tag
FROM tf_bh_trade a,tf_b_trade_score b
WHERE trade_type_code IN (360,417)
AND a.user_id=:USER_ID
AND cancel_tag IN ('0','1')
AND a.trade_id=b.trade_id