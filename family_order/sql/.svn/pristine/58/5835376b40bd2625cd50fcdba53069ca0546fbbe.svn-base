SELECT PARTITION_ID,
       TO_CHAR(USER_ID) USER_ID,
       TO_CHAR(ACCT_ID) ACCT_ID,
       PAYITEM_CODE,
       ACCT_PRIORITY,
       USER_PRIORITY,
       BIND_TYPE,
       DEFAULT_TAG,
       ACT_TAG,
       LIMIT_TYPE,
       TO_CHAR(LIMIT) LIMIT,
       COMPLEMENT_TAG
  FROM (SELECT PARTITION_ID,
               TO_CHAR(USER_ID) USER_ID,
               TO_CHAR(ACCT_ID) ACCT_ID,
               PAYITEM_CODE,
               ACCT_PRIORITY,
               USER_PRIORITY,
               BIND_TYPE,
               START_CYCLE_ID,
               END_CYCLE_ID,
               DEFAULT_TAG,
               ACT_TAG,
               LIMIT_TYPE,
               TO_CHAR(LIMIT) LIMIT,
               COMPLEMENT_TAG,
               UPDATE_STAFF_ID,
               UPDATE_DEPART_ID,
               TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME
          FROM TF_A_PAYRELATION A
         WHERE A.ACCT_ID = TO_NUMBER(:ACCT_ID)
           AND A.DEFAULT_TAG = :DEFAULT_TAG
           AND A.ACT_TAG = :ACT_TAG
           AND TO_CHAR(SYSDATE, 'YYYYMMDD') BETWEEN A.START_CYCLE_ID AND
               A.END_CYCLE_ID
           AND NOT EXISTS
         (SELECT 1
                  FROM TF_F_USER_PRODUCT
                 WHERE USER_ID = A.USER_ID
                   AND PARTITION_ID = MOD(A.USER_ID, 10000)
                   AND (BRAND_CODE LIKE '%IP%' OR BRAND_CODE = 'PBAR')))
 GROUP BY PARTITION_ID,
          USER_ID,
          ACCT_ID,
          PAYITEM_CODE,
          ACCT_PRIORITY,
          USER_PRIORITY,
          BIND_TYPE,
          DEFAULT_TAG,
          ACT_TAG,
          LIMIT_TYPE,
          LIMIT,
          COMPLEMENT_TAG