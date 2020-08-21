UPDATE TF_F_USER_FOREGIFT
   SET MONEY             = MONEY + TO_NUMBER(:MONEY),
       FOREGIFT_IN_DATE  = TO_DATE(:FOREGIFT_IN_DATE,
                                   'YYYY-MM-DD HH24:MI:SS'),
       FOREGIFT_OUT_DATE = TO_DATE(:FOREGIFT_OUT_DATE,
                                   'YYYY-MM-DD HH24:MI:SS'),
       UPDATE_TIME       = TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),
       CUST_NAME         = :CUST_NAME,
       PSPT_ID           = :PSPT_ID,
       UPDATE_STAFF_ID   = :UPDATE_STAFF_ID,
       UPDATE_DEPART_ID  = :UPDATE_DEPART_ID,
       REMARK            = :REMARK,
       RSRV_STR5         = :RSRV_STR5
 WHERE USER_ID = TO_NUMBER(:USER_ID)
   AND FOREGIFT_CODE = :FOREGIFT_CODE