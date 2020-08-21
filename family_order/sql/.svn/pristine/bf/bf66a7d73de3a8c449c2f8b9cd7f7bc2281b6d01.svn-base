SELECT COUNT(1) recordcount
FROM tf_f_user_other
WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
AND user_id=TO_NUMBER(:USER_ID)
AND rsrv_value_code=:RSRV_VALUE_CODE
AND (rsrv_str1=:RSRV_STR1 or :RSRV_STR1='*')
AND (rsrv_str2=:RSRV_STR2 or :RSRV_STR2='*')
AND sysdate BETWEEN start_date+0 AND end_date+0