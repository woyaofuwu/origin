delete from tf_f_user_otherserv
where  user_id=TO_NUMBER(:USER_ID)
 AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
 AND SERVICE_MODE = '0'
   AND start_date = to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS')