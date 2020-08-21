INSERT INTO TF_F_USER_PLATSVC_ATTR
  SELECT MOD(A.USER_ID, 10000),
         A.USER_ID,
         A.SERVICE_ID,
         A.SERIAL_NUMBER,
         NULL,
         A.INFO_CODE,
         (DECODE(D.OPER_CODE,
                 '7',
                 DECODE(A.SERVICE_ID,
                        '98002401',
                        DECODE(A.INFO_CODE, '401', '00000', ''),
                        ''),
                 A.INFO_VALUE)),
         A.INFO_NAME,
         A.UPDATE_TIME,
         A.UPDATE_STAFF_ID,
         A.UPDATE_DEPART_ID,
         A.REMARK,
         A.RSRV_NUM1,
         A.RSRV_NUM2,
         A.RSRV_STR1,
         A.RSRV_STR2,
         A.RSRV_DATE1,
         A.RSRV_DATE2,
         A.RSRV_DATE3
    FROM TF_B_TRADE_PLATSVC_ATTR A,
         TD_B_PLATSVC_ATTR       C,
         TF_B_TRADE_PLATSVC      D
   WHERE A.SERVICE_ID = C.SERVICE_ID
     AND A.INFO_CODE = C.ATTR_CODE
     AND NOT EXISTS
   (SELECT 1
            FROM TF_F_USER_PLATSVC_ATTR B
           WHERE B.PARTITION_ID = MOD(:USER_ID, 10000)
             AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
             AND A.TRADE_ID = :TRADE_ID
             AND B.USER_ID = A.USER_ID
             AND B.SERVICE_ID = A.SERVICE_ID
             AND B.INFO_CODE = A.INFO_CODE)
     AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
     AND A.TRADE_ID = :TRADE_ID
     AND A.USER_ID = :USER_ID
     AND A.SERVICE_ID = :SERVICE_ID
     AND A.ACCEPT_MONTH = D.ACCEPT_MONTH
     AND A.TRADE_ID = D.TRADE_ID
     AND A.USER_ID = D.USER_ID
     AND A.SERVICE_ID = D.SERVICE_ID