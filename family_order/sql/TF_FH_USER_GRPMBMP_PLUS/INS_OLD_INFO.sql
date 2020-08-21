INSERT INTO tf_fh_user_grpmbmp_plus(partition_id,user_id,trade_id,x_tag,biz_code,info_code,info_value,rsrv_str1,rsrv_str2,rsrv_str3)
SELECT partition_id,user_id,to_number(:TRADE_ID),x_tag,biz_code,info_code,info_value,rsrv_str1,rsrv_str2,rsrv_str3
  FROM tf_f_user_grpmbmp_plus
 where partition_id = mod(to_number(:USER_ID),10000)
   and user_id = to_number(:USER_ID)