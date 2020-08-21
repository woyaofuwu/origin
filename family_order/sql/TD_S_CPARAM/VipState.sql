--IS_CACHE=Y
SELECT 'VipState' KEY, paramcode VALUE1, '' VALUE2, paramvalue VRESULT
  FROM td_m_groupext
 WHERE 'VipState' = :KEY
   and paramtype = 'VipState'