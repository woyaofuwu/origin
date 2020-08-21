UPDATE TF_B_CTRM_ORDER
   SET ORDER_STATUS       = :ORDER_STATUS,
       ACCEPT_DATE        = to_date(:ACCEPT_DATE, 'yyyy-MM-dd hh24:mi:ss'),
       ACCEPT_RESULT    = :ACCEPT_RESULT,
       ERROR_RESULT     = :ERROR_RESULT,
       UPDATE_TIME        = to_date(:UPDATE_TIME, 'yyyy-MM-dd hh24:mi:ss'),
       UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,
       UPDATE_DEPART_ID = :UPDATE_DEPART_ID
 WHERE OID = :OID
   AND TID = :TID