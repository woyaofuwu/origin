--IS_CACHE=Y
SELECT bank_code,bank,eparchy_code,city_code,super_bank_code,bank_inner_code,contact,contact_phone,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_bank
 WHERE eparchy_code=:EPARCHY_CODE 
   AND super_bank_code=:SUPER_BANK_CODE
   AND (city_code=:CITY_CODE OR :CITY_CODE='HNSJ')