UPDATE tf_f_user_discnt
   SET end_date=TO_DATE(:END_DATE1, 'YYYY-MM-DD HH24:MI:SS'),update_time=sysdate
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND discnt_code=:DISCNT_CODE
   AND end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')