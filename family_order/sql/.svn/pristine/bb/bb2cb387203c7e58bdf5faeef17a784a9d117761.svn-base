--IS_CACHE=Y
SELECT /*+INDEX(a PK_TD_M_RES_COMMPARA)*/  eparchy_code,para_attr,para_code1,para_code2,para_name,para_value1,para_value2,para_value3,para_value4,para_value5,para_value6,para_value7,para_value8,to_char(para_value9) para_value9,to_char(para_value10) para_value10,to_char(para_value11) para_value11,to_char(para_value12) para_value12,to_char(rdvalue1,'yyyy-mm-dd hh24:mi:ss') rdvalue1,to_char(rdvalue2,'yyyy-mm-dd hh24:mi:ss') rdvalue2,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_res_commpara a
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=:PARA_ATTR
   AND (:PARA_CODE1 IS NULL OR  para_code1=:PARA_CODE1)
   AND (:PARA_CODE2 IS NULL OR  para_code2=:PARA_CODE2)
   AND (:PARA_VALUE2 IS NULL OR  para_value2=:PARA_VALUE2)
   AND (:PARA_VALUE4 IS NULL OR  para_value4=:PARA_VALUE4)
   AND (:PARA_VALUE7='@' OR ( para_value7=:PARA_VALUE7 
   and para_value5>=to_char(sysdate,'YYYY-MM-DD   HH24:MI:SS')) )