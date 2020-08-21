SELECT COUNT(1) recordcount
  FROM tf_f_user_platsvc
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND biz_type_code=:BIZ_TYPE_CODE
   AND (sp_code=:SP_CODE OR :SP_CODE IS NULL)
   AND (biz_code=:BIZ_CODE OR :BIZ_CODE IS NULL)
   AND biz_state_code='A'
   AND SYSDATE BETWEEN start_date AND end_date