--IS_CACHE=Y
SELECT 'groupAttr' KEY, paramcode VALUE1, '' VALUE2, paramvalue VRESULT
  FROM td_m_groupext
 WHERE 'groupAttr' = :KEY
       and paramtype = 'groupAttr'