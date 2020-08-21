select count(*) recordcount from tf_bh_trade a,tf_b_trade_score b,tf_b_trade_scoresub c
    where a.user_id = :USER_ID AND A.TRADE_TYPE_CODE = 330 AND A.TRADE_ID = B.TRADE_ID AND a.TRADE_ID = C.TRADE_ID
    AND C.ACTION_CODE IN (SELECT ACTION_CODE FROM td_b_score_action a where a.exchange_type_code = :EXCHANGE_CODE)
    AND A.ACCEPT_DATE > :DATE_TIME AND A.CANCEL_TAG = '0' and b.score_changed <=-5000