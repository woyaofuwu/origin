--IS_CACHE=Y
SELECT to_char(acyc_id) paracode,to_char(bcyc_id) paraname
FROM td_a_acycpara
where acyc_id > 288
AND (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)
order by acyc_id