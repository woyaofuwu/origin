--IS_CACHE=Y
SELECT 'ResKindName'  KEY, eparchy_code VALUE1, res_type_code VALUE2, res_kind_code VALUE3, kind_name VRESULT
  FROM td_s_reskind
 WHERE 'ResKindName' = :KEY