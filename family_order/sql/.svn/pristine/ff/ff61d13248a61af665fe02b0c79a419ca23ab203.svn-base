--IS_CACHE=Y
SELECT  /*+INDEX(a PK_TD_M_RES_COMMPARA)*/ eparchy_code,para_attr,para_code1,para_code2,para_name,para_value1,para_value2,para_value3,para_value4,para_value5,para_value6,para_value7,para_value8,to_char(para_value9) para_value9,to_char(para_value10) para_value10,to_char(para_value11) para_value11,to_char(para_value12) para_value12,to_char(rdvalue1,'yyyy-mm-dd hh24:mi:ss') rdvalue1,to_char(rdvalue2,'yyyy-mm-dd hh24:mi:ss') rdvalue2,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_res_commpara a
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=:PARA_ATTR
   AND (:PARA_CODE1 IS NULL OR para_code1=:PARA_CODE1)
   AND (:PARA_CODE2 IS NULL OR para_code2=:PARA_CODE2)
   AND (:PARA_VALUE1 IS NULL OR para_value1=:PARA_VALUE1)
   AND (:PARA_VALUE2 IS NULL OR para_value2=:PARA_VALUE2)
   AND (:PARA_VALUE3 IS NULL OR para_value3=:PARA_VALUE3)
   AND TO_DATE(para_value8,'YYYY-MM-DD') >= TO_DATE(:PARA_VALUE4,'YYYY-MM-DD')
   AND TO_DATE(para_value8,'YYYY-MM-DD') <= TO_DATE(:PARA_VALUE5,'YYYY-MM-DD')
   AND (:PARA_VALUE6 IS NULL OR para_value6 =:PARA_VALUE6)
   AND (:PARA_VALUE7 IS NULL OR para_value7 =:PARA_VALUE7)
   AND (:PARA_VALUE8 IS NULL OR :PARA_VALUE8 =:PARA_VALUE8)
   AND (:PARA_VALUE9 IS NULL OR para_value9=:PARA_VALUE9)