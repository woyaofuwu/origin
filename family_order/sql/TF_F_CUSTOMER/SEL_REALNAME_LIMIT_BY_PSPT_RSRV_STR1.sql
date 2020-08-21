Select PSPT_TYPE_CODE, PSPT_ID, CUST_NAME, LIMIT_COUNT, LIMIT_TAG, EPARCHY_CODE, UPDATE_STAFF_ID, UPDATE_DEPART_ID, RSRV_STR1, RSRV_STR2, RSRV_STR3, REMARK 
From TF_F_CUST_PSPT_LIMIT 
Where PSPT_ID = :PSPT_ID 
And CUST_NAME = :CUST_NAME
And RSRV_STR1 = :RSRV_STR1