UPDATE TF_F_USER_PLAT_REGISTER a
   SET a.END_DATE = TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')-1 / 24 / 3600
WHERE a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id=TO_NUMBER(:USER_ID)
   AND sysdate BETWEEN a.start_date+0 AND a.end_date+0
   AND a.BIZ_STATE_CODE = :BIZ_STATE_CODE
   AND EXISTS(SELECT 1 FROM td_s_commpara 
              WHERE param_attr = :PARAM_ATTR 
              AND param_code = to_char(a.BIZ_TYPE_CODE) 
              AND sysdate BETWEEN start_date AND end_date)