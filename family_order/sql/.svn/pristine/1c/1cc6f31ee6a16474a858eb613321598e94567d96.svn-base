UPDATE tf_f_user_mbmp
   SET end_date=TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')-1/24/3600
 WHERE user_id=:USER_ID AND partition_id=MOD(:USER_ID,10000)
  AND biz_type_code=:BIZ_TYPE_CODE
  AND end_date>SYSDATE