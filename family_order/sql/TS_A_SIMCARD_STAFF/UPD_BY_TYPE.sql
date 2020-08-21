UPDATE ts_a_simcard_staff
   SET total_num=TO_NUMBER(:TOTAL_NUM),oper_staff_id=:OPER_STAFF_ID,oper_depart_id=:OPER_DEPART_ID,oper_time=sysdate,remark=:REMARK  
 WHERE staff_id=:STAFF_ID
   AND sim_type_code=:SIM_TYPE_CODE
   AND (:CAPACITY_TYPE_CODE is null or capacity_type_code=:CAPACITY_TYPE_CODE)
   AND in_date=to_char(sysdate,'yyyymmdd')