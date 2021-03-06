SELECT A.USER_ID,
	   (SELECT D.RSRV_DATE2
          FROM TF_B_TRADE_SALE_ACTIVE D
         WHERE D.TRADE_ID = (SELECT C.RELATION_TRADE_ID
                               FROM TF_B_TRADE_SALE_ACTIVE C
                              WHERE C.TRADE_ID = :TRADE_ID
                                AND C.MODIFY_TAG = 1)
           AND D.MODIFY_TAG = 0) RSRV_DATE2,
       (SELECT D.END_DATE
          FROM TF_B_TRADE_SALE_ACTIVE D
         WHERE D.TRADE_ID = (SELECT C.RELATION_TRADE_ID
                               FROM TF_B_TRADE_SALE_ACTIVE C
                              WHERE C.TRADE_ID = :TRADE_ID
                                AND C.MODIFY_TAG = 1)
           AND D.MODIFY_TAG = 0) END_DATE,
	   A.END_DATE AS END_TIME,
	   B.START_DATE, 
       A.MONTHS,
	   A.RSRV_STR25,
       A.RELATION_TRADE_ID,
       A.UPDATE_STAFF_ID,
       A.UPDATE_DEPART_ID,
	   SUBSTR(B.REMARK,0,12) REMARK,
	   B.RSRV_DATE3,
	   B.PRODUCT_ID,
       ((EXTRACT(year FROM B.END_DATE) - EXTRACT(year FROM A.END_DATE)) * 12 +
       EXTRACT(month FROM B.END_DATE) - EXTRACT(month FROM A.END_DATE)) END_OFFSET,
       ((EXTRACT(year FROM A.END_DATE) - EXTRACT(year FROM A.START_DATE)) * 12 +
       EXTRACT(month FROM A.END_DATE) - EXTRACT(month FROM A.START_DATE)) START_OFFSET
  FROM TF_B_TRADE_SALE_ACTIVE     A,
       TF_F_USER_SALE_ACTIVE B
 WHERE A.TRADE_ID = :TRADE_ID
   AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
   AND A.MODIFY_TAG = :MODIFY_TAG
   AND B.RELATION_TRADE_ID = A.RELATION_TRADE_ID
   AND B.USER_ID = A.USER_ID
   AND B.PARTITION_ID = MOD(TO_NUMBER(A.USER_ID), 10000)