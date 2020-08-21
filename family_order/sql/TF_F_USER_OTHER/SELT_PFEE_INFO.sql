select rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str8, rsrv_str10 from tf_b_trade_other 
where trade_id = :TRADE_ID and rsrv_value_code = :RSRV_VALUE_CODE