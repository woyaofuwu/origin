insert into tf_fh_user_mbmp value(
select * from tf_f_user_mbmp where user_id=:TRADE_ID and biz_type_code=:BIZ_TYPE_CODE)