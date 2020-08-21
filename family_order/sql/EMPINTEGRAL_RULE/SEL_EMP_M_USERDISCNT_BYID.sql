--IS_CACHE=N
SELECT COUNT(1) 业务数
  FROM TF_F_USER_DISCNT T
 WHERE T.USER_ID = TO_NUMBER(:USER_ID)
   AND T.PARTITION_ID = MOD(T.USER_ID, 10000)
   AND T.DISCNT_CODE IN (select regexp_substr(:V_DISCNT_CODE, '[^,]+', 1, level)
          from dual
        connect by regexp_substr(:V_DISCNT_CODE, '[^,]+', 1, level) is not null)
   AND T.START_DATE <=
       ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE, 'YYYYMM'), 'YYYYMM'), TO_NUMBER(:V_START_DATE))
   AND T.END_DATE >=
       TRUNC(LAST_DAY(ADD_MONTHS(SYSDATE, TO_NUMBER(:V_END_DATE))) + 1) - 1 / 86400