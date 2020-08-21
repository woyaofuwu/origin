--IS_CACHE=Y
SELECT n.eparchy_code,moffice_id,n.switch_id,t.switch_type_code,serialnumber_s,serialnumber_e,imsi_s,imsi_e
  FROM td_m_moffice n,td_m_switch t
 WHERE  t.switch_id = n.switch_id
   AND (t.eparchy_code = n.eparchy_code or t.eparchy_code = 'ZZZZ') 
   AND :SERIAL_NUMBER between n.serialnumber_s and n.serialnumber_e 
   AND n.eparchy_code=:EPARCHY_CODE