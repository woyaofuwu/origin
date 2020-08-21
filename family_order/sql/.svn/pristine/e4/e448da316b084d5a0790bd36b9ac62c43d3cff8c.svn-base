--IS_CACHE=Y
SELECT DISTINCT(A.para_code1) para_code1,
  (A.para_name) para_name, 
  (A.para_value9) para_value9,            
  (A.para_value1) para_value1,           
  NVL(F_RES_GETCODENAME('area_code',A.para_value1, '', ''),'') para_value5,  
  (A.para_value3) para_value3,
  DECODE(A.para_value3,'0','开放','1','禁用',A.para_value3) para_value6, 
  (A.para_value2) para_value2,
  DECODE(A.para_value2,'0','地州级','1','业务区',A.para_value2) para_value7,  
  (B.para_code1) para_code2,(B.para_name) para_value8 ,0 x_tag                    
 FROM td_m_res_commpara A,td_m_res_commpara B
 WHERE A.para_attr=1044
  AND  B.para_attr=1045   
  AND  A.para_code2 = B.para_code1   
  AND  A.para_code2 <> '$$$$$'
  AND  A.para_code1 like :PARA_CODE1
  AND  A.eparchy_code= B.eparchy_code  
  AND  A.eparchy_code =:EPARCHY_CODE