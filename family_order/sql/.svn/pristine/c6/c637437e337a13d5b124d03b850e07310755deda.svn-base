UPDATE tf_f_user_brandchange
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') - 1/24/3600
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND end_date > sysdate