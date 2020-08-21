INSERT INTO td_m_res_commpara(eparchy_code,para_attr,para_code1,para_code2,para_name,para_value1,para_value2,para_value3,para_value4,para_value5,para_value6,para_value7,para_value8,para_value9,para_value10,para_value11,para_value12,rdvalue1,rdvalue2,remark,update_time,update_staff_id,update_depart_id)
select b.eparchy_code,b.para_attr,b.para_code1,:STOCK_ID_IN,b.para_name,b.para_value1,:NUM,b.para_value3,b.para_value4,
b.para_value5,b.para_value6,b.para_value7,b.para_value8,b.para_value9,b.para_value10,b.para_value11,b.para_value12,
sysdate,b.rdvalue2,:REMARK,sysdate,:OPER_STAFF_ID,:OPER_DEPART_ID from td_m_res_commpara b
WHERE eparchy_code = :EPARCHY_CODE
and para_attr = :PARA_ATTR
and para_code1 = :PARA_CODE1
and para_code2 = :PARA_CODE2