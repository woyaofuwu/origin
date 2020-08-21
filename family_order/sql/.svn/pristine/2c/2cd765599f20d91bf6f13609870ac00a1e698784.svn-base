INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,discnt_code,spec_tag,relation_type_code,start_date,end_date,update_time) 
SELECT TO_NUMBER(:PARTITION_ID),TO_NUMBER(:USER_ID),TO_NUMBER(:USER_ID_A),to_number(PARAM_CODE),'2',:RELATION_TYPE_CODE,trunc(sysdate),TO_DATE('2050-12-31 23:59:59','YYYY-MM-DD HH24:MI:SS'),sysdate
 FROM td_s_commpara a
 WHERE a.subsys_code = 'CSM'
   AND a.param_attr = 6018
   AND sysdate between a.start_date and a.end_date