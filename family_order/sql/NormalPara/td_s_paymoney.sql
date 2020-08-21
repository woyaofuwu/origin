--IS_CACHE=Y
SELECT pay_money_code paracode,pay_money paraname FROM td_s_paymoney
 WHERE (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)
order by 1