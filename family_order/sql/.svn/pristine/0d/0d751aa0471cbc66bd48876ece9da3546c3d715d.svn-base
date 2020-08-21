SELECT PARTITION_ID,
       TO_CHAR(USER_ID) USER_ID,
       A.SERVICE_ID,
       A.BIZ_STATE_CODE,
       TO_CHAR(A.FIRST_DATE, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE,
       TO_CHAR(A.FIRST_DATE_MON, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE_MON,
       A.GIFT_SERIAL_NUMBER,
       A.GIFT_USER_ID,
       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       A.RSRV_NUM1,
       A.RSRV_NUM2,
       A.RSRV_NUM3,
       TO_CHAR(A.RSRV_NUM4) RSRV_NUM4,
       TO_CHAR(A.RSRV_NUM5) RSRV_NUM5,
       TO_CHAR(A.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       TO_CHAR(A.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       TO_CHAR(A.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       A.RSRV_TAG1,
       A.RSRV_TAG2,
       A.RSRV_TAG3 
  FROM TF_F_USER_PLATSVC A
  WHERE PARTITION_ID = MOD(:VUSER_ID,10000)
    AND USER_ID = :VUSER_ID
    AND (A.BIZ_STATE_CODE = 'A' OR A.BIZ_STATE_CODE = 'N' OR A.BIZ_STATE_CODE = 'L')
    AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE
