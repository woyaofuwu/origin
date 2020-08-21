--IS_CACHE=Y
SELECT eparchy_code,moffice_id,switch_id,serialnumber_s,serialnumber_e,imsi_s,imsi_e
  FROM td_m_moffice
 WHERE eparchy_code=:EPARCHY_CODE
   AND serialnumber_s<=:SERIAL_NUMBER
   AND serialnumber_e>=:SERIAL_NUMBER