UPDATE tf_a_consignlog
   SET return_tag=:RETURN_TAG,return_eparchy_code=:RETURN_EPARCHY_CODE,
       return_time=sysdate,return_city_code=:RETURN_CITY_CODE,
       return_staff_id=:RETURN_STAFF_ID,return_depart_id=:RETURN_DEPART_ID,
       refuse_reason_code=:REFUSE_REASON_CODE  
 WHERE consign_id=TO_NUMBER(:CONSIGN_ID)