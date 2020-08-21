--IS_CACHE=Y
select a.param_code, a.param_name, a.para_code1
  from td_s_commpara a
 where a.subsys_code = :SUBSYS_CODE
   and a.param_attr = :PARAM_ATTR
   and sysdate between a.start_date and a.end_date
   and a.para_code1 = :PRODUCT_ID