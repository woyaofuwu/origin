--IS_CACHE=Y
SELECT a.visit_prov_code,
       NVL(A.VISIT_AREA_CODE, B.AREA_CODE) VISIT_AREA_CODE,
       decode(a.msc, '*', '未知', '*') msc,
       a.lac,
       a.cell_id,
       a.home_prov_code,
       a.home_area_code,
       a.edge_flag,
       a.begin_time,
       a.end_time,
       a.lac_hex,
       a.cell_id_hex,
       a.remark
  FROM UCR_PARAM.TD_EDGE_ROAM A, UCR_PARAM.TD_LAC B
 WHERE A.LAC = B.LAC(+)
   and (:VISIT_PROV_CODE is null or a.visit_prov_code = :VISIT_PROV_CODE)
   AND (:VISIT_AREA_CODE is null or a.visit_area_code = :VISIT_AREA_CODE)
   AND (:LAC is null or a.lac = :LAC)
   AND (:CELL_ID is null or a.cell_id = :CELL_ID)
   AND (:HOME_PROV_CODE is null or a.home_prov_code = :HOME_PROV_CODE)
   AND (:HOME_AREA_CODE is null or a.home_area_code = :HOME_AREA_CODE)
   AND (:EDGE_FLAG is null or a.edge_flag = :EDGE_FLAG)
   AND (:BEGIN_TIME is null or a.begin_time >= :BEGIN_TIME)
   AND (:END_TIME is null or :END_TIME >= a.end_time )
   AND (:LAC_HEX is null or a.lac_hex = :LAC_HEX)
   AND (:CELL_ID_HEX is null or a.cell_id_hex = :CELL_ID_HEX)