select * from TF_F_USER_PLATSVC where 
user_id=(select user_id from tf_F_user where serial_number=:SERIAL_NUMBER and remove_tag='0')
and biz_type_code = :BIZ_TYPE_CODE
and service_id in (98003701,98006701)
and rsrv_str5=:INTF_TRADE_ID