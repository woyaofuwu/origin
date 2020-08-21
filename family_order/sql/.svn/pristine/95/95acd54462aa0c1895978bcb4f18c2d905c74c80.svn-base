UPDATE tf_a_consignlog
SET return_tag='0',
return_eparchy_code=:RETURN_EPARCHY_CODE,
return_time=sysdate,
return_city_code=:RETURN_CITY_CODE,
return_staff_id=:RETURN_STAFF_ID,
return_depart_id=:RETURN_DEPART_ID,
refuse_reason_code=:REFUSE_REASON_CODE , 
consign_eparchy_code=:CONSIGN_EPARCHY_CODE,
consign_city_code=:CONSIGN_CITY_CODE,
consign_staff_id=:CONSIGN_STAFF_ID,
consign_depart_id=:CONSIGN_DEPART_ID,
rsrv_number2=TO_NUMBER(:RSRV_NUMBER2),
RSRV_TAG2 ='1'  
WHERE consign_id=TO_NUMBER(:CONSIGN_ID)