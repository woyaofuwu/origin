SELECT count(*) recordcount
  FROM tf_f_cust_vip a,tf_f_cust_interest c
 WHERE a.usecust_id = c.cust_id(+)
  AND (:PARAM0 is null  or a.serial_number<=:PARAM0)
  AND (:PARAM1 is null or a.serial_number>=:PARAM1)
  AND (:PARAM2 is null or a.city_code =:PARAM2)
  AND (:PARAM3 is null or a.vip_type_code =:PARAM3)
  AND (:PARAM4 is null or a.class_id =:PARAM4)
  AND (:PARAM5 is null or a.cust_manager_id =:PARAM5)
  AND (:PARAM6  is null  or a.birthday >=to_date(:PARAM6,'yyyy-mm-dd'))
  AND (:PARAM7 is null or a.birthday <=to_date(:PARAM7,'yyyy-mm-dd'))
  AND (:PARAM8 is null or a.device_no =:PARAM8)
  AND (:PARAM9 is null or a.sex =:PARAM9)
  AND (:PARAM10 is null or a.local_native_code =:PARAM10)
  AND (:PARAM11 is null or c.interest_code =:PARAM11)
  AND a.remove_tag='0'
  AND EXISTS
     ( SELECT 1
       FROM  td_m_staff b,td_m_depart c
       WHERE a.cust_manager_id = b.staff_id
       and   b.depart_id = c.depart_id
       and c.depart_frame like (
           select t.depart_frame from td_m_depart t where t.depart_id= :PARAM12)||'%'
      )