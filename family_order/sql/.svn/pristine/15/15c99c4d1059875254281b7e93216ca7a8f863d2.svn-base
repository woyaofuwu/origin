--IS_CACHE=Y
SELECT  detail_item param_name
FROM TD_A_DETAILITEM a,TD_A_PAYITEM b
WHERE b.payitem_code=:PAYITEM_CODE
AND ( b.detail_itemset LIKE '%|'||a.detail_item_code||'|%'
OR b.detail_itemset LIKE a.detail_item_code||'|%')