select net_code paracode,net_name paraname
from td_s_net
WHERE (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)