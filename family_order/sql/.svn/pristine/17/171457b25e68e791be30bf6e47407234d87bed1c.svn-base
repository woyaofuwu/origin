--IS_CACHE=N
SELECT NVL(SUM(FEE), 0) 月费用
  FROM (SELECT A.FEE FEE
          FROM TMP_TS_B_BILL A, TF_A_PAYRELATION B
         WHERE A.INTEGRATE_ITEM_CODE IN (select regexp_substr(:INTEGRATE_ITEM_CODE, '[^,]+', 1, level)
          from dual
        connect by regexp_substr(:INTEGRATE_ITEM_CODE, '[^,]+', 1, level) is not null)
           AND A.ACCT_ID = B.ACCT_ID
		   AND B.DEFAULT_TAG='1'
           AND A.USER_ID = B.USER_ID
           AND B.USER_ID = :USER_ID
           AND B.PARTITION_ID = MOD(:USER_ID, 10000)
           AND A.PARTITION_ID = MOD(B.ACCT_ID, 10000)
           AND A.CYCLE_ID = TO_NUMBER(:V_MONTH)
        UNION
        SELECT A.FEE FEE
          FROM TMP_TS_BH_BILL A, TF_A_PAYRELATION B
         WHERE A.INTEGRATE_ITEM_CODE IN (select regexp_substr(:INTEGRATE_ITEM_CODE, '[^,]+', 1, level)
          from dual
        connect by regexp_substr(:INTEGRATE_ITEM_CODE, '[^,]+', 1, level) is not null)
           AND A.ACCT_ID = B.ACCT_ID
		   AND B.DEFAULT_TAG='1'
           AND A.USER_ID = B.USER_ID
           AND B.USER_ID = :USER_ID
           AND B.PARTITION_ID = MOD(:USER_ID, 10000)
           AND A.PARTITION_ID = MOD(B.ACCT_ID, 10000)
           AND A.CYCLE_ID = TO_NUMBER(:V_MONTH))