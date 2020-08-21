--IS_CACHE=Y
SELECT para_code1,para_code2,para_value11,para_value9
   FROM td_m_res_commpara
  WHERE para_attr=:PARA_ATTR
   AND  para_code1 like :PARA_CODE1
   AND  eparchy_code=:EPARCHY_CODE