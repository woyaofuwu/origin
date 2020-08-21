UPDATE tf_f_user_discnt
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') - 1/24/3600,
        update_time=sysdate
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND spec_tag=:SPEC_TAG
   AND relation_type_code=:RELATION_TYPE_CODE
   AND end_date>=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')