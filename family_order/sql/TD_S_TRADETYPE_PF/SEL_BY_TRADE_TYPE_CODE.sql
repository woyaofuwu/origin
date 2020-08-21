--IS_CACHE=Y
SELECT trade_type_code, obj_name, rsrv_str1, rsrv_str2, rsrv_str3 
 FROM  TD_S_TRADETYPE_PF a
 WHERE a.trade_type_code = :TRADE_TYPE_CODE  or a.trade_type_code = -1