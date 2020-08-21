DELETE FROM ts_a_simcard_staff
 WHERE staff_id=:STAFF_ID
   AND sim_type_code=:SIM_TYPE_CODE
   AND (:CAPACITY_TYPE_CODE is null or capacity_type_code=:CAPACITY_TYPE_CODE)
   AND in_date=to_char(sysdate,'yyyymmdd')