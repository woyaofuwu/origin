--IS_CACHE=Y
SELECT  NVL(count(para_code2),0) para_code2 
  FROM  td_m_res_commpara
  WHERE para_attr=:PARA_ATTR
   AND  (:PARA_CODE1 IS NULL OR para_code1=:PARA_CODE1) 
   AND  (:PARA_CODE2 IS NULL OR para_code2=:PARA_CODE2) 
   AND  eparchy_code=:EPARCHY_CODE