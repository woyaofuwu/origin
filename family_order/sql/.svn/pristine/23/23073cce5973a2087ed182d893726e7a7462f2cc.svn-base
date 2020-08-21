SELECT SERIAL_NUMBER, TRUNC(sysdate - IN_DATE) AS DAYS
  FROM (SELECT t.IN_STAFF_ID, t.in_date, t.serial_number
          FROM TF_F_USER T
         WHERE t.in_date between sysdate - 31 and sysdate
           and T.ACCT_TAG = '2') A,
       TF_F_PRE_OPEN_STAFF B
 where A.IN_STAFF_ID = B.STAFF_CODE