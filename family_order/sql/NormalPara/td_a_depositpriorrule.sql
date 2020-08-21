--IS_CACHE=Y
SELECT DEPOSIT_CODE paracode,DEPOSIT_NAME paraname FROM td_a_depositpriorrule where DEPOSIT_PRIOR_RULE_ID=0 AND eparchy_code=:TRADE_EPARCHY_CODE