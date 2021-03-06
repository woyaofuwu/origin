SELECT BOMC_ID,
       WORKFORMID,
       WORKFORM_NAME,
       ACCEPT_MONTH,
       OPER_TYPE,
       DEAL_STATE,
       BOMC_ORDER_CODE,
       TO_CHAR(INSERT_TIME, 'yyyy-MM-dd HH24:mi:ss') as INSERT_TIME,
       TO_CHAR(UPDATE_TIME, 'yyyy-MM-dd HH24:mi:ss') as UPDATE_TIME,
       SHEETTYPE,
       TO_CHAR(DEAL_LIMIT, 'yyyy-MM-dd HH24:mi:ss') as DEAL_LIMIT,
       ATTACHREF,
       OPDETAIL,
       ERRLIST,
       TO_CHAR(RESULT_TIME, 'yyyy-MM-dd HH24:mi:ss') as RESULT_TIME,
       CITY_CODE,
       EPARCHY_CODE,
       DEPART_ID,
       DEPART_NAME,
       STAFF_ID,
       STAFF_NAME,
       STAFF_PHONE,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       RSRV_STR6,
       RSRV_STR7,
       REMARK,
       RENEW_REASON,
       DEAL_PERSON,
       CONTACT_PERSON,
       CONTACT_PHONE,
       IS_CORRECT,
       ELIMINATE_TIME,
       PROBLEM_REASON,
       DEALDESC,
       DEALRESULT
FROM   TF_B_EOP_BOMC B
WHERE  1 = 1
AND    B.WORKFORMID = :WORKFORMID
