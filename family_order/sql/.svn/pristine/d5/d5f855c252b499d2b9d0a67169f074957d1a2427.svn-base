UPDATE tf_f_user_discnt
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),
       update_staff_id=:UPDATE_STAFF_ID,
       update_depart_id=:UPDATE_DEPART_ID,
        update_time=sysdate
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND discnt_code=TO_NUMBER(:DISCNT_CODE)
   AND spec_tag=:SPEC_TAG
   AND inst_id=TO_NUMBER(:INST_ID)
   AND end_date < TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')