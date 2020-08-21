select  T.*
  from tf_f_user_other t
 where t.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND t.user_id=TO_NUMBER(:USER_ID)
   AND t.RSRV_VALUE_CODE = 'RED_PAK'
   AND T.RSRV_VALUE = '2'
   and t.RSRV_STR17 = '0'
	 and t.rsrv_str10 = :PACKAGE_ID
   and sysdate < t.end_date