--IS_CACHE=Y
SELECT a.param_attr,a.param_code trade_type_code,
       a.param_name class_name,a.para_code1 reg_exp,
       a.para_code2 com_tag,a.para_code3 element_type_code
       FROM td_s_commpara a
       WHERE A.SUBSYS_CODE = :SUBSYS_CODE
       AND A.PARAM_ATTR = :PARAM_ATTR
       AND A.PARAM_CODE = :PARAM_CODE
       AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE