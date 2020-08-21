UPDATE tf_a_comlot
   SET return_time         = sysdate,
       return_tag          = :RETURN_TAG,
       return_eparchy_code = :RETURN_EPARCHY_CODE,
       return_city_code    = :RETURN_CITY_CODE,
       return_staff_id     = :RETURN_STAFF_ID,
       return_depart_id    = :RETURN_DEPART_ID,
       refuse_reason_code  = :REFUSE_REASON_CODE
 WHERE mconsign_id = TO_NUMBER(:MCONSIGN_ID)
   AND nvl(return_tag, '0') = :IN_RETURN_TAG