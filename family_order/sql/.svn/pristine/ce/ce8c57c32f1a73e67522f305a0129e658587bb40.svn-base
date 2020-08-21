SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,inst_id,campn_id,
to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
 FROM tf_f_user_discnt a 
  WHERE a.user_id=:USER_ID
  AND a.discnt_code IN (SELECT B.PARA_CODE1 FROM TD_S_COMMPARA B 
                        WHERE B.SUBSYS_CODE=:SUBSYS_CODE 
                          AND B.PARAM_ATTR=:PARAM_ATTR
                          AND B.PARAM_CODE=:PARAM_CODE
                          AND SYSDATE BETWEEN B.START_DATE AND B.END_DATE
                          AND (B.EPARCHY_CODE=:EPARCHY_CODE OR B.EPARCHY_CODE='ZZZZ'))
  AND SYSDATE  < a.end_date
