--IS_CACHE=Y
select PARAM_CODE,PARA_CODE1 from td_s_commpara a where a.subsys_code='CSM' and a.param_attr='998'
and( a.param_code = :OLD_BRAND_CODE OR a.param_code =:BRAND_CODE)