--IS_CACHE=Y
SELECT INTEGRATE_ITEM_CODE paracode,INTEGRATE_ITEM paraname
FROM td_a_integrateitem
where eparchy_code = :TRADE_EPARCHY_CODE OR eparchy_code='ZZZZ'