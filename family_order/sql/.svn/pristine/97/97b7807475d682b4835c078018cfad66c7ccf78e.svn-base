update TF_B_TRADE_PLATSVC set rsrv_str10=:INTF_TRADE_ID,rsrv_str9=:TRADE_ID,INTF_TRADE_ID=:INTF_TRADE_ID
where trade_id=:TRADE_ID
and accept_month=substr(:TRADE_ID,5,2)
and biz_type_code = :BIZ_TYPE_CODE
and service_id in (98003701,98006701)