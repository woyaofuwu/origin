UPDATE TF_F_USER_PLATSVC A
   SET A.END_DATE = TO_DATE(:ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') -
                    1 / 24 / 3600
 WHERE A.PARTITION_ID = MOD(:USER_ID, 10000)
   AND A.USER_ID = :USER_ID
   AND A.SERVICE_ID NOT LIKE '98%' --开关服务,全退订服务等
   AND A.BIZ_TYPE_CODE IN (SELECT BIZ_TYPE_CODE
                             FROM TD_B_PLATSVC_LIMIT
                            WHERE OPER_CODE = '02'
                              AND LIMIT_SVCID = :SERVICE_ID
                              AND LIMIT_SVCSTATE = 'A'
                              AND LIMIT_TYPE = '0'
                              AND LIMIT_SVCTYPE = '0')
   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE