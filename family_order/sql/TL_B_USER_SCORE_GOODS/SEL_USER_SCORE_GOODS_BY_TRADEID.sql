select t.*  from TL_B_USER_SCORE_GOODS t
where t.user_id=:USER_ID
and t.trade_id=:TRADE_ID