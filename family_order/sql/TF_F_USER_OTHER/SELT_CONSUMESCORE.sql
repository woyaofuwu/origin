SELECT TO_CHAR(ABS(NVL(SUM(score_changed),0))) score_changed
FROM tf_bh_trade a,tf_b_trade_score b
WHERE a.user_id=TO_NUMBER(:USER_ID)
AND cancel_tag='0'
AND score_changed<0
AND a.trade_id=b.trade_id
AND TO_CHAR(ACCEPT_DATE,'YYYYMM')=:BCYC_ID