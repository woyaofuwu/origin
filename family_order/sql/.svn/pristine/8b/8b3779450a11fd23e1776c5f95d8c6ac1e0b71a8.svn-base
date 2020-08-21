--IS_CACHE=Y
SELECT a.subsys_code,a.param_attr,
       a.param_code element_id,
       a.param_name confirm_message,
       a.para_code1 method_name,
       a.para_code2 element_type_code
       FROM td_s_commpara a
       WHERE a.subsys_code=:SUBSYS_CODE
       AND   a.param_attr=:PARAM_ATTR
       AND   a.param_code=:ELEMENT_ID
       AND   a.para_code2=:ELEMENT_TYPE_CODE
       AND   SYSDATE BETWEEN a.start_date AND a.End_Date