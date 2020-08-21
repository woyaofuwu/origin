--IS_CACHE=Y
SELECT a.eparchy_code,a.moffice_id,a.switch_id,a.serialnumber_s,a.serialnumber_e,a.imsi_s,a.imsi_e
  FROM td_m_moffice a,td_m_moffice b
 WHERE :SERIAL_NUMBER BETWEEN a.serialnumber_s AND a.serialnumber_e
   AND :SERIAL_NUMBER_CODE BETWEEN b.serialnumber_s AND b.serialnumber_e
   AND a.eparchy_code = b.eparchy_code
   AND a.switch_id = b.switch_id