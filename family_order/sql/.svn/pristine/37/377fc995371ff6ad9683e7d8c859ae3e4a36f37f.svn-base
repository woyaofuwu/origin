--IS_CACHE=Y
SELECT eparchy_code,F_SYS_GETCODENAME('area_code',eparchy_code, '', '') area_name,moffice_id,switch_id,serialnumber_s,serialnumber_e,imsi_s,imsi_e,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,F_SYS_GETCODENAME('staff_id',update_staff_id, '', '') staff_name,update_depart_id,F_SYS_GETCODENAME('depart_id',update_depart_id, '', '') DEPART_NAME,0 x_tag
  FROM td_m_moffice
 WHERE eparchy_code=:EPARCHY_CODE
   AND ((:MOFFICE_ID IS NOT NULL AND moffice_id=:MOFFICE_ID) OR :MOFFICE_ID IS NULL)