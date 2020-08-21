update TF_F_USER_PLATSVC set rsrv_str10=:INTF_TRADE_ID ,rsrv_str9=:TRADE_ID
where user_id=:USER_ID
and partition_id=mod(:USER_ID,10000)
and biz_type_code = :BIZ_TYPE_CODE
and service_id in (98003701,98006701)
and sysdate between start_date and end_date