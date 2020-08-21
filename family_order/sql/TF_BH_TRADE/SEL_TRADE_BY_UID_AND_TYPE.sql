select t.* from tf_bh_trade t 
    where t.user_id=:USER_ID 
    and t.trade_type_code=:TRADE_TYPE_CODE