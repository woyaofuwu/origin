--IS_CACHE=Y
SELECT eparchy_code,moffice_id,switch_id,serialnumber_s,serialnumber_e,imsi_s,imsi_e,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id
FROM td_m_moffice
 WHERE eparchy_code=:EPARCHY_CODE
   AND :SIM_CARD_NO BETWEEN SUBSTR(serialnumber_s,3,2) AND SUBSTR(serialnumber_e,3,2)
   AND substr(moffice_id,2,length(moffice_id)-1) NOT IN ('130003','130004','130009','150002')
   AND rownum<2