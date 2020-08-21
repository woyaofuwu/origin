SELECT *
  FROM (SELECT CONSIGN_ID,
               SERIAL_NUMBER,
               ACCT_ID,
               PAY_NAME,
               B.BCYC_ID BCYC_ID,
               ASPAY_FEE,
               BANK_CODE,
               BANK_ACCT_NO,
               BANK
          FROM TF_A_CONSIGNLOG A, TD_A_ACYCPARA B
         WHERE ((RETURN_TAG = '0' AND
               TRUNC(RECV_TIME) >= TO_DATE(:START_TIME, 'yyyy-mm-dd') AND
               TRUNC(RECV_TIME) <= TO_DATE(:END_TIME, 'yyyy-mm-dd')) OR
               (RETURN_TAG <> '0' AND
               TRUNC(RETURN_TIME) >= TO_DATE(:START_TIME, 'yyyy-mm-dd') AND
               TRUNC(RETURN_TIME) <= TO_DATE(:END_TIME, 'yyyy-mm-dd'))) AND
               ASPAY_FEE <> 0 AND A.ACYC_ID = B.ACYC_ID AND
               BANK_CODE IN
               (SELECT BANK_CODE FROM TD_B_BANK WHERE SUPER_BANK_CODE LIKE  :SUPER_BANK_CODE||'%') AND
               RETURN_TAG LIKE :RETURN_TAG||'%' AND CITY_CODE LIKE :CITY_CODE||'%'
         ORDER BY CONSIGN_ID)
 WHERE CONSIGN_ID > TO_NUMBER(:CONSIGN_ID) AND ROWNUM <= 1000