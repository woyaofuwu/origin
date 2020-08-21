select res_no
from   tf_b_res_middle
where  log_id=to_number(:LOG_ID)
and    res_type_code='0'
and    RES_STATE_CODE NOT IN('2','3','4','5')
and    res_no like :RSRV_STR2
and    stock_level = :STOCK_LEVEL
AND    (:AGENT_ID is NULL OR stock_id like :AGENT_ID)  
AND    (:RSRV_STR4 is NULL OR RSRV_STR4 LIKE :RSRV_STR4)
ORDER   BY res_no