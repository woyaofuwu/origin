UPDATE tf_a_paylog_rc
   SET cancel_tag='1',cancel_time=TO_DATE(:CANCEL_TIME,'YYYY/MM/DD HH24:MI:SS'),cancel_eparchy_code=:CANCEL_EPARCHY_CODE,cancel_city_code=:CANCEL_CITY_CODE,cancel_depart_id=:CANCEL_DEPART_ID,cancel_staff_id=:CANCEL_STAFF_ID 
 WHERE charge_id=TO_NUMBER(:CHARGE_ID)
   AND partition_id= :PARTITION_ID
   AND cancel_tag = '0'