select agencyid paracode,agencyname paraname
from TF_R_AGENCY
where (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)
order by agencyid asc