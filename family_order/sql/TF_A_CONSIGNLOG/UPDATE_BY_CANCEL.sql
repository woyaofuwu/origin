UPDATE tf_a_consignlog
   SET return_tag='0',return_eparchy_code=null,
       return_time=null,return_city_code=null,
       return_staff_id=null,return_depart_id=null,
       refuse_reason_code=null  
 WHERE consign_id=TO_NUMBER(:CONSIGN_ID)