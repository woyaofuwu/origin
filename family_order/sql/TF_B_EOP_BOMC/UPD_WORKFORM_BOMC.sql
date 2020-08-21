UPDATE TF_B_EOP_BOMC T
SET    T.BOMC_ID         = :NEW_BOMC_ID,
       T.WORKFORM_NAME   = :WORKFORM_NAME,
       T.ACCEPT_MONTH    = :ACCEPT_MONTH,
       T.OPER_TYPE       = :OPER_TYPE,
       T.DEAL_STATE      = :DEAL_STATE,
       T.BOMC_ORDER_CODE = :BOMC_ORDER_CODE,
       T.UPDATE_TIME     = to_date(:UPDATE_TIME, 'yyyy-MM-dd hh24:Mi:ss'),
       T.SHEETTYPE       = :SHEETTYPE,
       T.DEAL_LIMIT      = to_date(:DEAL_LIMIT, 'yyyy-MM-dd hh24:Mi:ss'),
       T.ATTACHREF       = :ATTACHREF,
       T.OPDETAIL        = :OPDETAIL,
       T.ERRLIST         = :ERRLIST,
       T.CITY_CODE       = :CITY_CODE,
       T.EPARCHY_CODE    = :EPARCHY_CODE,
       T.DEPART_ID       = :DEPART_ID,
       T.DEPART_NAME     = :DEPART_NAME,
       T.STAFF_ID        = :STAFF_ID,
       T.STAFF_NAME      = :STAFF_NAME,
       T.STAFF_PHONE     = :STAFF_PHONE,
       T.RSRV_STR1       = :RSRV_STR1,
       T.RSRV_STR2       = :RSRV_STR2,
       T.RSRV_STR3       = :RSRV_STR3,
       T.RSRV_STR4       = :RSRV_STR4,
       T.RSRV_STR5       = :RSRV_STR5,
       T.RSRV_STR6       = :RSRV_STR6,
       T.RSRV_STR7       = :RSRV_STR7,
       T.REMARK          = :REMARK,
       T.RENEW_REASON    = :RENEW_REASON
WHERE  1 = 1
AND    T.WORKFORMID = :WORKFORMID
AND    T.BOMC_ORDER_CODE = :BOMC_ORDER_CODE