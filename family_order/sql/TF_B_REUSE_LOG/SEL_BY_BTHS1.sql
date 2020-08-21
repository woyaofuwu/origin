SELECT b.eparchy_code eparchy_code,b.serial_number serial_number,a.destroy_time rsrv_date1,a.user_id rsrv_str1,
        DECODE(a.remove_tag,'2','主动销号','4','欠费销号','') rsrv_str2,
        b.city_code_o city_code_o,b.stock_id_o stock_id_o,b.back_time back_time,
        b.back_staff_id back_staff_id,d.switch_id rsrv_str3
  FROM tf_f_user a,tf_b_reuse_log b,td_m_moffice d
 WHERE b.back_time>=TO_DATE(:RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss')
   AND b.back_time<=TO_DATE(:RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss')
   AND a.eparchy_code=:EPARCHY_CODE
   AND b.eparchy_code=:EPARCHY_CODE
   AND d.eparchy_code=:EPARCHY_CODE
   AND a.serial_number=b.serial_number
   AND a.open_date=(SELECT MAX(c.open_date)
                      FROM tf_f_user c
                     WHERE c.serial_number=a.serial_number
                       AND c.remove_tag IN ('2', '4')
                   )
   AND b.moffice_id=d.moffice_id
   AND (:RES_NO_S IS NULL OR b.serial_number>=:RES_NO_S)
   AND (:RES_NO_E IS NULL OR b.serial_number<=:RES_NO_E)
   AND (:BACK_STAFF_ID IS NULL OR b.back_staff_id=:BACK_STAFF_ID)
   AND (:SWITCH_ID IS NULL OR d.switch_id=:SWITCH_ID)