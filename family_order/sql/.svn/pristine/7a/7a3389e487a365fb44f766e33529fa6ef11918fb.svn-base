UPDATE tf_a_noteprintlog_center
   SET cancel_tag='1',cancel_staff_id=:CANCEL_STAFF_ID,cancel_depart_id=:CANCEL_DEPART_ID,
cancel_city_code=:CANCEL_CITY_CODE,cancel_eparchy_code=:CANCEL_EPARCHY_CODE,
cancel_time=sysdate
 WHERE RSRV_INFO2=:PROCID
   AND cancel_tag='0'