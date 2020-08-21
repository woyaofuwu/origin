UPDATE Tf_f_User_Widenet SET RSRV_STR2 =:CF_PORT,RSRV_STR3 =:CF_ROOM,RSRV_STR4=:CF_TTAREA
where partition_id = mod(to_number(:USER_ID),10000)
and user_id = to_number(:USER_ID)
and sysdate between start_date and end_date