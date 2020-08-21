SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_discnt a
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND SYSDATE<end_date
   AND EXISTS(SELECT 1 FROM td_s_discnt_limit b
               WHERE discnt_code_a=:DISCNT_CODE_A
                 AND a.discnt_code=b.discnt_code_b
                 AND limit_tag=:LIMIT_TAG
                 AND sysdate BETWEEN start_date AND end_date
                 AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ'))
   AND EXISTS(SELECT 1 FROM td_s_discnt_limit c
               WHERE c.discnt_code_a=a.discnt_code
                 AND discnt_code_b=:DISCNT_CODE_A
                 AND limit_tag=:LIMIT_TAG
                 AND sysdate BETWEEN start_date AND end_date
                 AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ'))