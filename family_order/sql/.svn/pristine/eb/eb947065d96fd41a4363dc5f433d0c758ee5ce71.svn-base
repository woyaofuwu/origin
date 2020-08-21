UPDATE tf_a_noteprintlog
   SET cancel_tag='1',cancel_staff_id=:CANCEL_STAFF_ID,cancel_depart_id=:CANCEL_DEPART_ID,
cancel_city_code=:CANCEL_CITY_CODE,cancel_eparchy_code=:CANCEL_EPARCHY_CODE,
cancel_reason=:CANCEL_REASON  ,cancel_time=sysdate
 WHERE CHARGE_ID=TO_NUMBER(:CHARGE_ID)
   AND partition_id>=:START_PARTITION_ID
   AND partition_id<=:END_PARTITION_ID
   AND cancel_tag='0'