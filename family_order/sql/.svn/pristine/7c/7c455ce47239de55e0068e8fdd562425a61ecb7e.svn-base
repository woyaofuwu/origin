UPDATE TF_F_USER_PLATSVC A
   SET START_DATE         = DECODE(A.SP_CODE || '_' || A.BIZ_CODE,
                                   '699013_20830001',
                                   DECODE(A.START_DATE,
                                          TRUNC(LAST_DAY(SYSDATE) + 1),
                                          SYSDATE,
                                          A.START_DATE),
                                   A.START_DATE),
       END_DATE           = DECODE(A.SP_CODE || '_' || A.BIZ_CODE,
                                   '699013_20830001',
                                   DECODE(A.START_DATE,
                                          TRUNC(LAST_DAY(SYSDATE) + 1),
                                          SYSDATE - 1 / 24 / 3600,
                                          TO_DATE(:END_DATE,
                                                  'YYYY-MM-DD HH24:MI:SS') -
                                          1 / 24 / 3600),
                                   TO_DATE(:END_DATE,
                                           'YYYY-MM-DD HH24:MI:SS') -
                                   1 / 24 / 3600),
       REMARK   = '全退订须终止相关订购关系',
       A.UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,
       A.UPDATE_DEPART_ID = :UPDATE_DEPART_ID
WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
     AND USER_ID = TO_NUMBER(:USER_ID)
     AND BIZ_STATE_CODE IN ('A', 'N')
     AND ORG_DOMAIN = :ORG_DOMAIN
     AND ((SYSDATE BETWEEN A.START_DATE AND A.END_DATE) OR
       (A.END_DATE > SYSDATE AND
       A.SERVICE_ID IN
       (SELECT SERVICE_ID FROM TD_B_PLATSVCBOOK_CONFIG)))
     AND SP_CODE NOT LIKE 'SW%'
     AND EXISTS (SELECT 1
            FROM TD_B_PLATSVC B
           WHERE A.SERVICE_ID = B.SERVICE_ID
             AND (B.SERV_TYPE = '1' OR (B.ORG_DOMAIN IN('RINP','MRBT') AND B.SERV_TYPE = '0')))