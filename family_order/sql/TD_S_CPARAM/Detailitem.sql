--IS_CACHE=Y
SELECT 'Detailitem' key, DETAIL_ITEM_CODE value1,'-1' value2, DETAIL_ITEM vresult
  FROM TD_A_DETAILITEM
 WHERE 'Detailitem' = :KEY