--IS_CACHE=Y
SELECT 'ResAssignName' KEY, eparchy_code VALUE1, res_type_code VALUE2, res_trade_type_code VALUE3,
res_trade_type VRESULT
FROM TD_S_RES_TRADETYPE
 WHERE 'ResAssignName' = :KEY