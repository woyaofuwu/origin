UPDATE tf_f_user_other
   SET rsrv_str11='',rsrv_str12='',remark='异地写卡返销！'
WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND rsrv_str12=:RSRV_STR12
   AND sysdate < end_date+0