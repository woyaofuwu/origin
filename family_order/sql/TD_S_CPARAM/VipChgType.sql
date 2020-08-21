--IS_CACHE=Y
SELECT 'VipChgType' KEY, paramcode VALUE1, '' VALUE2, paramvalue VRESULT
  FROM td_m_groupext
 WHERE 'VipChgType' = :KEY
   and paramtype = 'VipChgType'