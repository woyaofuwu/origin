select stock_id paracode,stock_name  paraname
from tf_r_resstock_arch
where (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)
order by stock_id asc