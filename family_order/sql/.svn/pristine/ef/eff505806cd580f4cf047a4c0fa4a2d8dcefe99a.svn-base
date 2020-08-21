update tf_f_user_other t
set t.RSRV_VALUE=:RSRV_VALUE,t.RSRV_STR16=to_char(sysdate,'YYYYMMDDHH24MISS'),T.RSRV_STR25=:RSRV_STR25
where t.user_id=TO_NUMBER(:USER_ID)
and t.RSRV_VALUE_CODE='RED_PAK'
and t.RSRV_VALUE='2'
and t.RSRV_STR10=:PACKAGE_ID
and t.RSRV_STR17='0'
and sysdate < t.end_date