UPDATE TF_B_NOTEPRINTLOG T
   SET CANCEL_TAG          = '0',
       CANCEL_TIME         = '',
       CANCEL_STAFF_ID     = '',
       CANCEL_DEPART_ID    = '',
       CANCEL_CITY_CODE    = '',
       CANCEL_EPARCHY_CODE = '',
       CANCEL_REASON_CODE  = ''
 WHERE T.RSRV_FEE1 = TO_NUMBER(:FEE_MODE)
   AND TRADE_ID = TO_NUMBER(:TRADE_ID)