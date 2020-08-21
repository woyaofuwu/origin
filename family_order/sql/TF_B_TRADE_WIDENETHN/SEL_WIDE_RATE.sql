--IS_CACHE=Y
select * from Td_s_Commpara t where t.subsys_code='CSM' and t.param_attr='4000' and t.param_code=:PARAM_CODE and ( t.eparchy_code='ZZZZ' or t.eparchy_code=:EPARCHY_CODE )