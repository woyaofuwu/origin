DELETE FROM tf_fh_user_grpmbmp
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND end_date>sysdate