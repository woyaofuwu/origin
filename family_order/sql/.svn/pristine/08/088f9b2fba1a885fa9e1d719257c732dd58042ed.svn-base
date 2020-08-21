--IS_CACHE=Y
select count(*) recordcount
from td_s_commpara
where param_attr=72
  and subsys_code='CSM'
  and param_code=:PARAM_CODE
  and para_code3=:PARA_CODE3
  and (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')