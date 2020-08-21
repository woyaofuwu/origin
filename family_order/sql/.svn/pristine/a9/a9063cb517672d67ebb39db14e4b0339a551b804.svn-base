INSERT INTO td_m_res_commpara
 (para_attr,eparchy_code,para_code1,para_code2,para_name,para_value1,
 para_value2,update_time,update_staff_id,update_depart_id)
 SELECT 1046,B.eparchy_code,'$$$$$',A.depart_id,A.depart_name,A.area_code,
 DECODE(B.code_type_code,'0','0','1'),sysdate,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID
  FROM td_m_depart A,td_m_departkind B
  WHERE A.depart_id=:PARA_CODE2
   AND  A.depart_kind_code= B.depart_kind_code 
   AND  B.eparchy_code=:EPARCHY_CODE