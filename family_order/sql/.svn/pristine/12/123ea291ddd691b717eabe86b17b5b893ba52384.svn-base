--IS_CACHE=Y
select factory_code paracode,factoryname paraname
from td_s_manufacturer
where (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)
order by factory_code asc