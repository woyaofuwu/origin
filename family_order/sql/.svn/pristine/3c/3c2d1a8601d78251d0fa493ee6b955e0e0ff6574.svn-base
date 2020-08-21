--IS_CACHE=Y
SELECT a.param_attr,a.param_code,a.para_code1
       FROM  td_s_commpara a
       WHERE a.subsys_code='CSM'
       AND   a.param_attr=:PARAM_ATTR
       AND   a.param_code=:PARAM_CODE
       AND   a.para_code1=:PARA_CODE1
       AND   SYSDATE BETWEEN a.start_date AND a.end_date