select t.*
  from tf_f_user_other t
 where t.user_id = :USER_ID
   and t.rsrv_value_code = 'CPE_LOCATION'
   and t.partition_id = mod(:USER_ID, 10000)