UPDATE tf_a_consignlog
   SET charge_id           = TO_NUMBER(:CHARGE_ID),
       return_tag          = :RETURN_TAG,
       return_eparchy_code = :RETURN_EPARCHY_CODE,
       return_time         = SYSDATE,
       return_city_code    = :RETURN_CITY_CODE,
       return_staff_id     = :RETURN_STAFF_ID,
       return_depart_id    = :RETURN_DEPART_ID,
       refuse_reason_code  = :REFUSE_REASON_CODE,
rsrv_tag2 = :RSRV_TAG2
 WHERE consign_id = TO_NUMBER(:CONSIGN_ID)
   AND NVL(return_tag,'0') = :IN_RETURN_TAG