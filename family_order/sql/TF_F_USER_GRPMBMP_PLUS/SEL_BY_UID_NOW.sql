SELECT a.partition_id,a.user_id,a.x_tag,a.biz_code,a.info_code,a.info_value,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3
  FROM tf_f_user_grpmbmp_plus a
 where a.user_id=to_number(:USER_ID)