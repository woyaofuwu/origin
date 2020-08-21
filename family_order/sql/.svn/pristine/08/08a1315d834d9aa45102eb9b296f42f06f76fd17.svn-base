UPDATE tf_f_user_discnt
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') ,update_time=sysdate ,REMARK =:REMARK
 WHERE
   user_id=TO_NUMBER(:USER_ID)
   AND INST_ID=TO_NUMBER(:INST_ID)
   AND discnt_code=:DISCNT_CODE
   AND end_date > sysdate