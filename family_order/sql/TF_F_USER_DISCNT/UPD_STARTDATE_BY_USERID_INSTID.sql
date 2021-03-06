UPDATE tf_f_user_discnt
   SET start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND discnt_code=:DISCNT_CODE
   AND inst_id=TO_NUMBER(:INST_ID)