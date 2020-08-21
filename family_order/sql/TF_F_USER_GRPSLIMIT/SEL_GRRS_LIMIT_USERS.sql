SELECT A.*
  FROM TF_F_USER_GRPSLIMIT A
 WHERE A.RSRV_STR1 = :RSRV_STR1
   AND A.update_time >= TRUNC(SYSDATE) - 1
   AND A.update_time < TRUNC(SYSDATE)
   AND A.USER_ID NOT IN (SELECT USER_ID
                           FROM TF_F_USER_OTHER B
                          WHERE B.RSRV_VALUE_CODE = 'GPRS_LIMIT'
                            AND B.USER_ID = A.USER_ID
                            AND B.RSRV_DATE1 > TRUNC(SYSDATE, 'mm')
                            AND B.END_DATE > SYSDATE)
   AND ROWNUM <= 200
