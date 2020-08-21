--IS_CACHE=Y
SELECT to_char(NVL(para_value10,0)) para_value10 
  FROM td_m_res_commpara
 WHERE para_attr=1038
   AND para_code1=:PARA_CODE1