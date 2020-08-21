--IS_CACHE=Y
SELECT  /*+INDEX(a PK_TD_M_RES_COMMPARA)*/ eparchy_code,para_attr,para_code1,para_code2,para_name,para_value1,para_value2,para_value3,para_value4,para_value5,para_value6,para_value7,para_value8,to_char(para_value9) para_value9,to_char(para_value10) para_value10,to_char(para_value11) para_value11,F_RES_GETCODENAME('area_code', para_value7, NULL, NULL) para_value12,decode(para_value9,0,'未使用',1,'已使用','未使用') rdvalue1,decode(para_value10,0,'白卡',1,'成卡','白卡') rdvalue2,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_res_commpara a
 WHERE (:EPARCHY_CODE IS NULL OR :EPARCHY_CODE IS NOT NULL) /*去掉EPARCHY_CODE条件*/
   AND para_attr=:PARA_ATTR
   AND (:PARA_CODE1 IS NULL OR para_code1=:PARA_CODE1)
   AND (:PARA_CODE2 IS NULL OR para_code2=:PARA_CODE2)
   AND (:PARA_VALUE1 IS NULL OR para_value1=:PARA_VALUE1)
   AND (:PARA_VALUE2 IS NULL OR para_value2=:PARA_VALUE2)
   AND (:PARA_VALUE3 IS NULL OR para_value3=:PARA_VALUE3)
   AND (:PARA_VALUE4 IS NULL OR para_value4=:PARA_VALUE4)
   AND (:PARA_VALUE5 IS NULL OR para_value5=:PARA_VALUE5)
   AND (:PARA_VALUE6 IS NULL OR para_value6=:PARA_VALUE6)
   AND (:PARA_VALUE7 IS NULL OR para_value7=:PARA_VALUE7)
   AND (:PARA_VALUE8 IS NULL OR para_value8=:PARA_VALUE8)
   AND (:PARA_VALUE9 IS NULL OR para_value9=:PARA_VALUE9)