delete from tf_f_user_other a
where user_id=to_number(:USER_ID)
  --and partition_id=mod(to_number(:USER_ID),10000)
  and rsrv_str10=:RSRV_STR10