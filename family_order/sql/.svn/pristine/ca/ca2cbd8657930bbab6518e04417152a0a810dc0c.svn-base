--IS_CACHE=Y
SELECT  integrate_item param_name 
FROM TD_A_INTEGRATEITEM a,TD_A_PAYITEM b
WHERE b.payitem_code=:PAYITEM_CODE
AND ( b.integrate_itemset LIKE '%|'||a.integrate_item_code||'|%'
OR b.integrate_itemset LIKE a.integrate_item_code||'|%')