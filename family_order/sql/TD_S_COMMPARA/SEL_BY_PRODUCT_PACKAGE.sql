--IS_CACHE=Y
select SUBSYS_CODE, PARAM_ATTR, PARAM_CODE, PARAM_NAME, PARA_CODE1
  from td_s_commpara A
 where  A.para_code1 = :PRODUCT_ID
   and A.param_attr =:PARAM_ATTR
   and A.param_code = '0'
   and A.subsys_code = 'CSM'
   and (A.eparchy_code = :EPARCHY_CODE or A.eparchy_code = 'ZZZZ')
   and A.end_date>sysdate
   UNION 
   select SUBSYS_CODE, PARAM_ATTR, PARAM_CODE, PARAM_NAME, PARA_CODE1
  from td_s_commpara b
 where  b.para_code1 = :PACKAGE_ID
   and b.param_attr =:PARAM_ATTR
   and b.param_code = '1'
   and b.subsys_code = 'CSM'
   and (b.eparchy_code = :EPARCHY_CODE or b.eparchy_code = 'ZZZZ')
   and b.end_date>sysdate