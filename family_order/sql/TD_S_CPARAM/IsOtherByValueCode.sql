SELECT /*+ index (a IDX_TF_F_USER_OTHER_VALUECODE)*/COUNT(1) recordcount
FROM tf_f_user_other a WHERE rsrv_value_code=:RSRV_VALUE_CODE
   AND rsrv_value=:RSRV_VALUE
   AND (rsrv_str1=:RSRV_STR1 OR :RSRV_STR1 IS NULL)
   AND (rsrv_str2=:RSRV_STR2 OR :RSRV_STR2 IS NULL)
   AND (rsrv_str3=:RSRV_STR3 OR :RSRV_STR3 IS NULL)
   AND (rsrv_str4=:RSRV_STR4 OR :RSRV_STR4 IS NULL)
   AND (rsrv_str5=:RSRV_STR5 OR :RSRV_STR5 IS NULL)
   AND (rsrv_str6=:RSRV_STR6 OR :RSRV_STR6 IS NULL)
   AND (rsrv_str7=:RSRV_STR7 OR :RSRV_STR7 IS NULL)
   AND end_date > SYSDATE
   AND end_date > start_date