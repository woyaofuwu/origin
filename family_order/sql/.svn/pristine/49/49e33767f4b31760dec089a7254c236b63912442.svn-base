update tf_f_user_other t
set t.RSRV_VALUE=:RSRV_VALUE,t.RSRV_STR10=:PACKAGE_ID,t.RSRV_STR11=:MID,t.RSRV_STR12=:MERID,t.RSRV_STR13=:ORDERID,T.RSRV_STR14=:AMT,T.RSRV_STR15=to_char(sysdate,'YYYYMMDDHH24MISS')
where t.user_id=TO_NUMBER(:USER_ID)
and t.RSRV_VALUE_CODE='RED_PAK'
and t.RSRV_VALUE='1'
and t.RSRV_STR17='0'
and sysdate < t.end_date