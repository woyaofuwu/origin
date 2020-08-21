--IS_CACHE=Y
SELECT 
  eparchy_code,para_attr,
  para_code1,para_name,
  para_code2,
  para_value1,
  NVL(F_RES_GETCODENAME('area_code',para_value1, '', ''),'') para_value5,
  para_value2,
  DECODE(para_value2,'0','地州级','1','业务区',para_value2) para_value6,
  para_value3,
  DECODE(para_value3,'0','开放','1','禁用',para_value3) para_value7,
  to_char(para_value9) para_value9,
  to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
  update_staff_id,update_depart_id,0 x_tag
  FROM td_m_res_commpara
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=1044
   AND para_value1=:PARA_VALUE1
   AND para_code2='$$$$$'