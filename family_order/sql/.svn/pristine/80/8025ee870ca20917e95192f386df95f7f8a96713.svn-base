select t.*
  from tf_F_user_other t
 where t.rsrv_value_code = 'FTTH_GROUP' 
   AND SYSDATE < t.END_DATE
   and user_id = :USER_ID