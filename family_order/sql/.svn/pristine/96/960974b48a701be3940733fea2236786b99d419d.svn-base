SELECT b.param_code, b.para_code2, b.param_name
 FROM tf_f_user_discnt a, td_s_commpara b
 WHERE 1=1
 AND a.user_id = :USER_ID
 AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
 AND a.discnt_code = b.param_code
 AND b.param_attr = '8872'
 AND SYSDATE BETWEEN a.start_date AND a.end_date
 AND sysdate BETWEEN b.start_date AND b.end_date
 AND (b.eparchy_code=:EPARCHY_CODE OR b.eparchy_code='ZZZZ') 
 AND b.subsys_code='CSM'