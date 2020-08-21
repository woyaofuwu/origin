update TF_M_STAFF_GRP_RIGHT T 
  set T.END_DATE = to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'),
      T.UPDATE_TIME = to_date(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss'),
      T.UPDATE_STAFF_ID = :UPDATE_STAFF_ID,
      T.UPDATE_DEPART_ID = :UPDATE_DEPART_ID
  where T.STAFF_ID = :STAFF_ID 
    and T.RIGHT_CODE = :RIGHT_CODE 
    and T.USER_PRODUCT_CODE = :USER_PRODUCT_CODE 
    and T.START_DATE = to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') 