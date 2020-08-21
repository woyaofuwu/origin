SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a
 WHERE partition_id=TO_NUMBER(:PARTITION_ID)
   AND user_id=TO_NUMBER(:USER_ID)   
   AND EXISTS(SELECT 1 FROM td_s_commpara WHERE param_attr =(:PARAM_ATTR)
              AND (para_code3 = a.discnt_code or para_code1 = a.discnt_code)
              AND (eparchy_code = :EPARCHY_CODE or eparchy_code = 'ZZZZ')
              AND sysdate BETWEEN start_date AND end_date)
   AND end_date > SYSDATE