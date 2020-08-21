--IS_CACHE=Y
SELECT 'ResGoodsMoveInfo' KEY, eparchy_code VALUE1, PARA_CODE2 VALUE2,PARA_NAME VRESULT
  FROM td_m_goods_para
 WHERE 'ResGoodsMoveInfo' = :KEY
   AND para_code1='GOODSMOVE'