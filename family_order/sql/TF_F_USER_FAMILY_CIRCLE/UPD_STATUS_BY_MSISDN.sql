UPDATE TF_F_USER_FAMILY_CIRCLE T SET T.STATUS = :STATUS,T.END_DATE = SYSDATE  
WHERE T.GROUP_TYPE = :GROUP_TYPE AND T.MAIN_MSISDN = :MAIN_MSISDN AND 
T.TARGET_MSISDN = :TARGET_MSISDN