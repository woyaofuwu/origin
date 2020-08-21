--IS_CACHE=Y
select a.param_code,
       a.param_name,
       a.para_code1,
       a.para_code2       
 from td_s_commpara a
 where a.subsys_code = :SUBSYS_CODE
   and a.param_attr = :PARAM_ATTR
   and a.param_code = :PARAM_CODE