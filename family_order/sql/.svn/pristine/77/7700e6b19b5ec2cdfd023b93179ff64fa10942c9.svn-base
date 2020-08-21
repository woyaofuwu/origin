select count(*) recordcount from tf_bh_trade a,tf_b_trade_score b
where a.user_id = :USER_ID AND A.TRADE_TYPE_CODE = 350 AND A.TRADE_ID = B.TRADE_ID
        AND A.ACCETP_DATE > :DATE_TIME AND a.trade_staff_id = :STAFF_ID AND A.CANCEL_TAG = '0' and b.score_changed <=-5000