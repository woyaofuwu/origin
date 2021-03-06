SELECT /*+ index(a IDX_TF_F_USER_OTHER_VALUECODE)*/partition_id,
       to_char(user_id) user_id,
       rsrv_value_code,
       rsrv_value,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5,
       rsrv_str6,
       rsrv_str7,
       rsrv_str8,
       rsrv_str9,
       rsrv_str10,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
FROM tf_f_user_other
Where rsrv_value_code ='HHAP' 
and user_id=to_number(:USER_ID) 
AND partition_id=MOD(to_number(:USER_ID),10000)
AND end_date > trunc(last_day(SYSDATE)) + 1