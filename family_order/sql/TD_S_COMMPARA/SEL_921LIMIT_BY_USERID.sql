SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,
spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
FROM tf_f_user_discnt 
WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
AND user_id=TO_NUMBER(:USER_ID)
AND end_date > SYSDATE
AND discnt_code IN
(
SELECT to_number(param_code) FROM td_s_commpara
WHERE SUBSYS_CODE='CSM'
AND param_attr=232
AND end_date > SYSDATE
AND eparchy_code='0898'
)