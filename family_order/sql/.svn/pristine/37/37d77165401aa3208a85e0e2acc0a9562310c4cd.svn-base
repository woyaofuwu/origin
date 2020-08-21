--IS_CACHE=Y
select a.subsys_code,a.param_attr,a.param_code,a.param_name,
       a.para_code1,a.para_code2,a.para_code3
       FROM td_s_commpara a
       WHERE A.SUBSYS_CODE = :SUBSYS_CODE
       AND A.PARAM_ATTR = :PARAM_ATTR
       AND A.PARAM_CODE = :PARAM_CODE 
       AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE