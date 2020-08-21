--IS_CACHE=Y
SELECT 'memberKind' KEY, paramcode VALUE1, '' VALUE2, paramvalue VRESULT
  FROM td_m_groupext
 WHERE 'memberKind' = :KEY
and paramtype = 'memberKind'