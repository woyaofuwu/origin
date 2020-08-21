--IS_CACHE=Y
SELECT 'ResParaNameE' KEY, eparchy_code VALUE1, PARA_CODE1 VALUE2,PARA_NAME VRESULT
  FROM td_m_assignpara
 WHERE 'ResParaNameE' = :KEY
       and PARA_ATTR = 1008