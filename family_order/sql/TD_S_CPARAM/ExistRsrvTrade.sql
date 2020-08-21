SELECT /*+ index (a PK_TF_B_TRADE)*/COUNT(1) recordcount
FROM tf_b_trade a WHERE trade_type_code =:TRADE_TYPE_CODE
AND (rsrv_str1=:RSRV_STR1 or :RSRV_STR1 is null)
AND (rsrv_str2=:RSRV_STR2 or :RSRV_STR2 is null)
AND (rsrv_str3=:RSRV_STR3 or :RSRV_STR3 is null)
AND trade_id <> :TRADE_ID
AND cancel_tag=:CANCEL_TAG