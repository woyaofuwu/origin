update Tf_f_User_Comp_Rela t set t.end_date = to_date(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),
update_time=sysdate,update_depart_id=:UPDATE_DEPART_ID,update_staff_id=:UPDATE_STAFF_ID
 where t.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
 and t.user_id=TO_NUMBER(:USER_ID)
 and t.comp_user_id=:COMP_USER_ID
 and sysdate < t.end_date