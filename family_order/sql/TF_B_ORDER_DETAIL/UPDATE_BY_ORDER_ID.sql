UPDATE TF_B_ORDER_DETAIL
   SET STATE   = :STATE,RSRV_TAG1 = :RSRV_TAG1,UPDATE_STAFF_ID = :UPDATE_STAFF_ID
 WHERE 1 = 1
   AND ORDER_ID = :ORDER_ID