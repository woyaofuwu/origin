select a.serial_number,b.switch_id moffice_id, a.remove_tag,a.destory_time,a.log_id,a.rsrv_str1,a.user_id,a.eparchy_code from tf_b_reuse_log a,td_m_moffice b 
WHERE a.back_time >= TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
AND a.back_time <= TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
and a.eparchy_code = :EPARCHY_CODE
and a.eparchy_code = b.eparchy_code
and a.moffice_id = b.moffice_id
and (:LOG_ID IS NULL OR a.LOG_ID = :LOG_ID)
and (:RES_NO_S IS NULL OR a.SERIAL_NUMBER >= :RES_NO_S)
and (:RES_NO_E IS NULL OR a.SERIAL_NUMBER <= :RES_NO_E)
and (:BACK_TYPE_CODE is null or a.back_type_code = :BACK_TYPE_CODE)
and (:MOFFICE_ID is null or b.switch_id = :MOFFICE_ID)
and (:BACK_STAFF_ID is null or a.back_staff_id = :BACK_STAFF_ID )