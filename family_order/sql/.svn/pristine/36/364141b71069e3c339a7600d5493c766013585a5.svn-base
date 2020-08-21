--IS_CACHE=Y
SELECT a.subsys_code,a.param_attr,a.param_code service_id,
       a.param_name service_name,a.para_code1 discnt_type_code
       FROM td_s_commpara a
       WHERE a.subsys_code=:SUBSYS_CODE
       AND   a.param_attr =:PARAM_ATTR
       AND   a.param_code =:SERVICE_ID
       AND   SYSDATE BETWEEN a.start_date AND a.end_date