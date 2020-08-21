SELECT a.PURCHASE_MODE paracode,a.PURCHASE_NAME paraname FROM td_b_purchasetype a
WHERE sysdate BETWEEN a.start_date AND a.end_date
   AND (a.eparchy_code = :TRADE_EPARCHY_CODE OR a.eparchy_code = 'ZZZZ')
   AND a.PURCHASE_MODE NOT IN
(SELECT b.para_code1 FROM td_s_commpara b WHERE b.param_attr=73 AND b.param_code='0' AND (b.eparchy_code = :TRADE_EPARCHY_CODE or b.eparchy_code = 'ZZZZ') AND b.subsys_code='CSM' AND sysdate BETWEEN b.start_date AND b.end_date)