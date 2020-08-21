SELECT a.eparchy_code eparchy_code,a.city_code city_code,b.switch_id rsrv_str3,
       DECODE(a.oper_type,'0','预配','1','返销','') rsrv_str6,
       b.serialnumber_s||'----'||b.serialnumber_e rsrv_str4,count(*) rsrv_str5
  FROM tf_b_importprecode_log a,td_m_moffice b
 WHERE a.moffice_id=b.moffice_id
   AND a.oper_date>=TO_DATE(:RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss')
   AND a.oper_date<=TO_DATE(:RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss')
   AND a.eparchy_code=:EPARCHY_CODE
   AND b.eparchy_code=:EPARCHY_CODE
   AND a.serial_number>=b.serialnumber_s AND a.serial_number<=b.serialnumber_e
   AND (:RES_NO_S IS NULL OR a.serial_number>=:RES_NO_S)
   AND (:RES_NO_E IS NULL OR a.serial_number<=:RES_NO_E)
   AND (:RES_CARD_S IS NULL OR a.sim_card_no>=:RES_CARD_S)
   AND (:RES_CARD_E IS NULL OR a.sim_card_no<=:RES_CARD_E)
   AND (:CITY_CODE IS NULL OR a.city_code=:CITY_CODE)
   AND (:SWITCH_ID IS NULL OR b.switch_id=:SWITCH_ID)
 GROUP BY a.eparchy_code,a.city_code,b.switch_id,DECODE(a.oper_type,'0','预配','1','返销',''),
          b.serialnumber_s,b.serialnumber_e