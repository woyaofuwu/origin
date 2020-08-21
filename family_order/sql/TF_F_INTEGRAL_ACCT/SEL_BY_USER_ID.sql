SELECT  PARTITION_ID      ,
 INTEGRAL_ACCOUNT_TYPE,
 NAME              ,
 USER_ID           ,
 ACCT_ID           ,
 CUST_ID           ,
 CONTRACT_PHONE    ,
 PASSWORD          ,
 EMAIL             ,
 PSPT_TYPE_CODE    ,
 PSPT_ID           ,
 ADDRESS           ,
 USE_LIMIT         ,
 START_DATE        ,
 END_DATE          ,
 STATUS            ,
 REMARK            ,
 RSRV_STR1         ,
 RSRV_STR2         ,
 RSRV_STR3         ,
 RSRV_STR4         ,
 RSRV_STR5         ,
 RSRV_STR6         ,
 RSRV_STR7         ,
 RSRV_STR8         ,
 RSRV_STR9         ,
 RSRV_STR10        ,
 RSRV_NUM1         ,
 RSRV_NUM2         ,
 RSRV_NUM3         ,
 RSRV_NUM4         ,
 RSRV_NUM5         ,
 RSRV_DATE1        ,
 RSRV_DATE2        ,
 RSRV_DATE3        ,
 RSRV_TAG1         ,
 RSRV_TAG2         ,
 RSRV_TAG3         ,
 INTEGRAL_ACCT_ID  
From TF_F_INTEGRAL_ACCT t
 WHERE T.USER_ID = TO_NUMBER(:USER_ID)
   AND STATUS = :STATUS
   AND SYSDATE BETWEEN START_DATE AND END_DATE