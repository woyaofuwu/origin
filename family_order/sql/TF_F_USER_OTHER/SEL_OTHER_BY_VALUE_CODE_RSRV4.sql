select  case when t.rsrv_date1>sysdate then '0' else '1' end RED_PAK_LIMIT_FLAG, T.*
  from tf_f_user_other t
 where t.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND t.user_id=TO_NUMBER(:USER_ID)
   AND t.RSRV_VALUE_CODE = 'RED_PAK'
   AND T.RSRV_VALUE = :RSRV_VALUE
   and t.RSRV_STR17 = '0'
   and sysdate < t.end_date