update tf_b_trade_purchase set imei = :NEWIMEI where trade_id in (
select trade_id from tf_bh_trade where user_id=:USER_ID and trade_type_code=240 )
and  imei=:OLDIMEI