select CANCEL_TAG,RSRV_STR5,TRADE_ID from tf_b_trade t where t.serial_number=:SERIAL_NUMBER and t.RSRV_STR7=:CHANNEL_TRADE_ID 
union 
select CANCEL_TAG,RSRV_STR5,TRADE_ID from tf_bh_trade t where t.serial_number=:SERIAL_NUMBER and t.RSRV_STR7=:CHANNEL_TRADE_ID