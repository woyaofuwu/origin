--IS_CACHE=Y
SELECT DEPOSIT_CODE paracode,DEPOSIT_NAME paraname FROM td_a_depositpriorrule
 WHERE present_tag='0' AND eparchy_code = :TRADE_EPARCHY_CODE
   AND deposit_prior_rule_id = 0 ORDER BY 1