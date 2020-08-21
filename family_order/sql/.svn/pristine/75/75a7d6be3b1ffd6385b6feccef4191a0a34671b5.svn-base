SELECT a.eparchy_code eparchy_code,a.city_code_o city_code_o,b.switch_id rsrv_str1,
        b.serialnumber_s||'----'||b.serialnumber_e rsrv_str2,count(*) rsrv_str3
  FROM tf_b_reuse_log a,td_m_moffice b
 WHERE a.moffice_id=b.moffice_id
   AND a.back_time>=TO_DATE(:RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss')
   AND a.back_time<=TO_DATE(:RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss')
   AND a.eparchy_code=:EPARCHY_CODE
   AND b.eparchy_code=:EPARCHY_CODE
   AND a.serial_number||''>=b.serialnumber_s AND a.serial_number||''<=b.serialnumber_e
   AND (:RES_NO_S IS NULL OR a.serial_number||''>=:RES_NO_S)
   AND (:RES_NO_E IS NULL OR a.serial_number||''<=:RES_NO_E)
   AND (:CITY_CODE IS NULL OR a.city_code_o=:CITY_CODE)
   AND (:SWITCH_ID IS NULL OR b.switch_id=:SWITCH_ID)
 GROUP BY a.eparchy_code,a.city_code_o,b.serialnumber_s,b.serialnumber_e,b.switch_id