--IS_CACHE=Y
SELECT 'VipChgKind' KEY, paramcode VALUE1, '' VALUE2, paramvalue VRESULT
  FROM td_m_groupext
 WHERE 'VipChgKind' = :KEY
   and paramtype = 'VipChgKind'