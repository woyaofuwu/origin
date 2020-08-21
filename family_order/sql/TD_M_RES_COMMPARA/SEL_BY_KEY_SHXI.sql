--IS_CACHE=Y
SELECT eparchy_code,para_attr,para_code1,para_code2,para_name,para_value1, decode(trunc(rdvalue1),trunc(sysdate),para_value2,'0') para_value2,para_value3,para_value4,para_value5,para_value6,para_value7,para_value8,to_char(para_value9) para_value9,to_char(para_value10) para_value10,to_char(para_value11) para_value11,to_char(para_value12) para_value12,to_char(rdvalue1,'yyyy-mm-dd hh24:mi:ss') rdvalue1,to_char(rdvalue2,'yyyy-mm-dd hh24:mi:ss') rdvalue2,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_res_commpara
 WHERE eparchy_code = :EPARCHY_CODE
  and para_attr = :PARA_ATTR
  and para_code1 = :PARA_CODE1
  and para_code2 = :PARA_CODE2
 and (:PARA_VALUE3 is null or para_value3=:PARA_VALUE3)