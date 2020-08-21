DELETE FROM tf_f_user_common
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
and user_id=TO_NUMBER(:USER_ID)