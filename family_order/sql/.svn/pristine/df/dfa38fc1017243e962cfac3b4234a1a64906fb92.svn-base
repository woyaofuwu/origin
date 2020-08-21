SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND user_id_a = TO_NUMBER(:USER_ID_A)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND relation_type_code = TO_NUMBER(:RELATION_TYPE_CODE) 
   AND spec_tag = '2'
   AND sysdate < end_date+0
   AND EXISTS (SELECT 1 FROM td_s_commpara 
                    WHERE subsys_code = 'CSM'
                      AND param_attr = 250
                      AND param_code = a.discnt_code
                      AND sysdate between start_date and end_date
              )