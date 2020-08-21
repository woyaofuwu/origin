SELECT A.UPDATE_TIME, 
A.SERIAL_NUMBER, 
B.RSRV_VALUE, 
B.RSRV_DATE1, 
B.RSRV_DATE2, 
A.GPRS_USED, 
A.GPRS_FREE, 
A.FREE_PERCENT * 100 || '%' FREE_PERCENT 
    FROM TF_F_USER_GRPSLIMIT A, TF_F_USER_OTHER B 
        WHERE 1 = 1 
        AND A.ROWID IN (SELECT MAX(C.ROWID) 
                                            FROM TF_F_USER_GRPSLIMIT C, TF_F_USER_OTHER D 
                                                WHERE 1 = 1 
                                                AND C.SERIAL_NUMBER = :SERIAL_NUMBER 
                                                AND D.USER_ID = C.USER_ID 
                                                AND D.RSRV_VALUE = :IS_LIMIT 
                                                AND D.RSRV_VALUE_CODE = 'GPRS_LIMIT' 
                                                AND C.UPDATE_TIME + 0 >= TO_DATE(:UPT_START, 'YYYY-MM-DD') 
                                                AND C.UPDATE_TIME + 0 < TO_DATE(:UPT_END, 'YYYY-MM-DD') + 1 
                                                GROUP BY C.SERIAL_NUMBER) 
        AND A.SERIAL_NUMBER = :SERIAL_NUMBER 
        AND B.USER_ID = A.USER_ID 
        AND B.RSRV_VALUE = :IS_LIMIT 
        AND B.RSRV_VALUE_CODE = 'GPRS_LIMIT' 
        AND A.UPDATE_TIME + 0 >= TO_DATE(:UPT_START, 'YYYY-MM-DD') 
        AND A.UPDATE_TIME + 0 < TO_DATE(:UPT_END, 'YYYY-MM-DD') + 1