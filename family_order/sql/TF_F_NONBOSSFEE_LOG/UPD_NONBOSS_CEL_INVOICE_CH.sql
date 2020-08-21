update TF_F_NONBOSSFEE_LOG t
set t.rsrv_str6=:CENCEL_TYPE,t.Rsrv_Str10=sysdate
where t.TRADE_ID=:TRADE_ID