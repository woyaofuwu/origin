INSERT INTO tf_b_trade_mbmp_plus(trade_id,user_id,serial_number,biz_type_code,org_domain,info_code,info_value)
 VALUES(TO_NUMBER(:TRADE_ID),TO_NUMBER(:USER_ID),:SERIAL_NUMBER,:BIZ_TYPE_CODE,:ORG_DOMAIN,:INFO_CODE,:INFO_VALUE)