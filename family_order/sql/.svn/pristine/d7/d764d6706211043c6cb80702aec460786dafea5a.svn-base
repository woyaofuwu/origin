INSERT INTO tf_fh_user_grpmbmp_plus(partition_id,user_id,trade_id,x_tag,biz_code,info_code,info_value,rsrv_str1,rsrv_str2,rsrv_str3)
SELECT a.partition_id,a.user_id,to_number(:TRADE_ID),a.x_tag,a.biz_code,a.info_code,a.info_value,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3
  FROM tf_f_user_grpmbmp_plus a
  where a.user_id=to_number(:USER_ID)
  and not exists(select 1 from tf_fh_user_grpmbmp_plus b 
                  where b.user_id=a.user_id
                  and b.trade_id=to_number(:TRADE_ID))