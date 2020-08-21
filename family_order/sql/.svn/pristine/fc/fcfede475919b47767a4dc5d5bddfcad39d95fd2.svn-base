UPDATE TF_F_USER_PLATSVC_ATTR A
	 SET A.INFO_VALUE       = (SELECT DECODE((SELECT OPER_CODE
                                                     FROM TF_B_TRADE_PLATSVC
                                                    WHERE ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                                                      AND TRADE_ID = :TRADE_ID
                                                      AND USER_ID = A.USER_ID
                                                      AND SERVICE_ID = A.SERVICE_ID 
                                                      AND ROWNUM = 1),
                                                   '7',
                                            DECODE(INFO_CODE,'401',DECODE(SERVICE_ID,'98002401','00000',''),''),
                                           INFO_VALUE)
                               FROM TF_B_TRADE_PLATSVC_ATTR
															WHERE ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
                                AND TRADE_ID = :TRADE_ID
                                AND USER_ID = :USER_ID
                                AND SERVICE_ID = :SERVICE_ID
                                AND USER_ID = A.USER_ID
                                AND SERVICE_ID = A.SERVICE_ID
                                AND INFO_CODE = A.INFO_CODE),
       A.UPDATE_TIME      = SYSDATE,
       A.UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,
       A.UPDATE_DEPART_ID = :UPDATE_DEPART_ID,
       A.RSRV_STR1 = DECODE(A.RSRV_STR1,'OVER_FLAG','',A.RSRV_STR1)
 WHERE A.PARTITION_ID = MOD(:USER_ID, 10000)
   AND A.USER_ID = :USER_ID
   AND A.SERVICE_ID = :SERVICE_ID
   AND EXISTS
 (SELECT 1
          FROM TF_B_TRADE_PLATSVC_ATTR B
         WHERE B.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
           AND B.TRADE_ID = :TRADE_ID
           AND B.USER_ID = :USER_ID
           AND B.SERVICE_ID = :SERVICE_ID
           AND B.USER_ID = A.USER_ID
           AND B.SERVICE_ID = A.SERVICE_ID
           AND B.INFO_CODE = A.INFO_CODE)