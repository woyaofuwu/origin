select * from TF_B_TRADE_PLATSVC where trade_id in 
(
select rsrv_str9 from TF_F_USER_PLATSVC
where user_id=:USER_ID
and PARTITION_id=mod(:USER_ID,10000)
and biz_type_code = :BIZ_TYPE_CODE
and service_id in (98003701,98006701)
)
and biz_type_code = :BIZ_TYPE_CODE
and service_id in (98003701,98006701) 
and INTF_TRADE_ID=:INTF_TRADE_ID