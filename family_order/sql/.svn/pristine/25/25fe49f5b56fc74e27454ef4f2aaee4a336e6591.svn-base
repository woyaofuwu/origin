--IS_CACHE=Y
SELECT a.PRODUCT_NAME paracode,f_sys_getcodename('grp_product_id',a.product_name,NULL,NULL)||'/'||a.PRODUCT_EXPLAIN paraname
FROM td_b_grp_product a
where (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)
and BRAND_CODE is not null