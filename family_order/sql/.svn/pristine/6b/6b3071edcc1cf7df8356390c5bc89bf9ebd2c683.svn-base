SELECT
DISTINCT 
a.partition_id,to_char(a.user_id_a) user_id_a,a.serial_number_a,to_char(a.user_id_b) 
user_id_b,a.serial_number_b,a.relation_type_code,a.role_code_a,a.role_code_b,a.orderno,
a.short_code,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char
(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date
FROM tf_f_relation_uu a ,td_a_acycpara b
WHERE a.end_date>=(b.acyc_end_time-1/24/60/60)
AND a.user_id_b=TO_NUMBER(:USER_ID_B)
AND a.partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
AND a.relation_type_code like '%'||:RELATION_TYPE_CODE||'%'
AND b.acyc_id BETWEEN :START_ACYC_ID AND :END_ACYC_ID