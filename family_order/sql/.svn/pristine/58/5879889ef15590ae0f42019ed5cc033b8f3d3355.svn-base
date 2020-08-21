--IS_CACHE=Y
SELECT eparchy_code,
F_SYS_GETCODENAME('area_code',eparchy_code, '', '') area_name,
para_attr,para_code1,para_code2,
para_name,
para_value1,para_value2,para_value3,
para_value4,para_value5,para_value6,
para_value7,para_value8,
to_char(para_value9) para_value9,to_char(para_value10) para_value10,
to_char(para_value11) para_value11,to_char(para_value12) para_value12,
to_char(rdvalue1,'yyyy-mm-dd hh24:mi:ss') rdvalue1,
to_char(rdvalue2,'yyyy-mm-dd hh24:mi:ss') rdvalue2,
remark,
to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
update_staff_id,
F_SYS_GETCODENAME('staff_id',update_staff_id, '', '') staff_name,
update_depart_id,
F_SYS_GETCODENAME('depart_id',update_depart_id, '', '') depart_name,0 x_tag
  FROM TD_M_RES_PARA
 WHERE eparchy_code=:EPARCHY_CODE
   AND ((:PARA_ATTR IS NOT NULL AND para_attr=:PARA_ATTR) OR :PARA_ATTR = -1)
   AND ((:PARA_CODE1 IS NOT NULL AND para_code1=:PARA_CODE1) OR :PARA_CODE1 IS NULL)
   AND ((:PARA_CODE2 IS NOT NULL AND para_code2=:PARA_CODE2) OR :PARA_CODE2 IS NULL)