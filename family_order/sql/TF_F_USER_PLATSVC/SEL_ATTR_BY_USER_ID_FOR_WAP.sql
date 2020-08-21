SELECT a.service_id,
	   a.info_code,
	   a.info_value,
       b.param_code,
       b.param_name,
       b.para_code1,
       b.para_code2
  FROM tf_f_user_attr a, td_s_commpara b
 WHERE a.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND a.service_id = b.PARAM_CODE
   AND a.attr_code = :INFO_CODE
   AND a.attr_value = b.PARA_CODE1
   AND b.PARAM_CODE = :PARAM_CODE
   AND b.SUBSYS_CODE = :SUBSYS_CODE
   AND b.param_attr = :PARAM_ATTR
   AND b.end_date > SYSDATE
   AND a.end_date > SYSDATE
   AND (b.eparchy_code = :EPARCHY_CODE OR b.eparchy_code = 'ZZZZ')