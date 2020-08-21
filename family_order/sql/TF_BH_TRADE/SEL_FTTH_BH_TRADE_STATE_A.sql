select t.*  
from tf_bh_trade t 
where t.subscribe_STATE='A' 
AND T.TRADE_TYPE_CODE='613' 
AND (T.RSRV_STR10 IS NULL OR T.RSRV_STR10 NOT IN ('1','99'))