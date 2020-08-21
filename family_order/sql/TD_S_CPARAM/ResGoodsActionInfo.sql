--IS_CACHE=Y
SELECT 'ResGoodsActionInfo' KEY, eparchy_code VALUE1, PARA_CODE2 VALUE2,PARA_NAME VRESULT
  FROM td_m_goods_para
 WHERE 'ResGoodsActionInfo' = :KEY
   AND para_code1='GOODSACTIONS'