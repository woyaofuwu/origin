SELECT a.eparchy_code eparchy_code,a.serial_number serial_number,a.imsi imsi,a.sim_card_no sim_card_no,
        b.switch_id rsrv_str3,TO_CHAR(a.oper_date,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,a.oper_staff_id oper_staff_id,
        DECODE(a.oper_type,'0','预配','1','返销','') rsrv_str4
 FROM tf_b_importprecode_log a,td_m_moffice b
 WHERE a.moffice_id=b.moffice_id
   AND a.oper_date>=TO_DATE(:RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss')
   AND a.oper_date<=TO_DATE(:RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss')
   AND a.eparchy_code=:EPARCHY_CODE
   AND b.eparchy_code=:EPARCHY_CODE
   AND (:RES_NO_S IS NULL OR a.serial_number>=:RES_NO_S)
   AND (:RES_NO_E IS NULL OR a.serial_number<=:RES_NO_E)
   AND (:RES_CARD_S IS NULL OR a.sim_card_no>=:RES_CARD_S)
   AND (:RES_CARD_E IS NULL OR a.sim_card_no<=:RES_CARD_E)
   AND (:OPER_STAFF_ID IS NULL OR a.oper_staff_id=:OPER_STAFF_ID)
   AND (:SWITCH_ID IS NULL OR b.switch_id=:SWITCH_ID)