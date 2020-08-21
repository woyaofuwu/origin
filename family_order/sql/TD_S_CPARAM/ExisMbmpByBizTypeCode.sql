SELECT COUNT(1) recordcount
  FROM tf_f_user_mbmp_sub
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND biz_type_code=:BIZ_TYPE_CODE
   AND (sp_id=:SP_ID OR :SP_ID IS NULL)
   AND (sp_svc_id=:SP_SVC_ID OR :SP_SVC_ID IS NULL)
   AND biz_state_code='A'
   AND SYSDATE BETWEEN start_date AND end_date