--IS_CACHE=Y
select supplystate paracode,statedesc paraname
from td_s_supplystate 
where (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)
order by supplystate asc