update TF_f_USER_OTHER t
set t.rsrv_str1=:RES_NO
WHERE t.rsrv_value_code = 'FTTH_GROUP' 
AND SYSDATE < t.END_DATE
and user_id = :USER_ID