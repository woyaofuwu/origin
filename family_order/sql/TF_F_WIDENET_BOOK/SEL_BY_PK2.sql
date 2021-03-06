SELECT A.CUST_NAME,
       A.CONTACT_SN,
       A.PSPT_TYPE_CODE,
       A.PSPT_ID,
       A.HOME_ADDR,
       A.SET_ADDR,
       A.PRE_PDTID,
       A.INST_ID,
       A.AUDIT_STATUS,
       TO_CHAR(A.REG_DATE, 'YYYY-MM-DD HH24:MI:SS') REG_DATE,
       A.UPDATE_TIME,
       A.UPDATE_STAFF_ID,
       A.UPDATE_DEPART_ID,
       A.REMARK,
       A.RSRV_NUM1,
       A.RSRV_NUM2,
       A.RSRV_NUM3,
       A.RSRV_NUM4,
       A.RSRV_NUM5,
       A.RSRV_STR1,
       A.RSRV_STR2 SERIAL_NUMBER,
       A.RSRV_STR3 AREA_CODE,
       A.RSRV_STR4 PRE_CAUSE,
       A.RSRV_STR5,
       TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD') PRE_DATE,
       A.RSRV_DATE2,
       A.RSRV_DATE3,
       A.RSRV_TAG1 WIDE_TYPE_CODE,
       A.RSRV_TAG2 WBBW,
       A.RSRV_TAG3 REG_STATUS
  FROM TF_F_WIDENET_BOOK A
 WHERE (A.REG_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD') OR :START_DATE IS NULL) 
   AND (A.REG_DATE <= TO_DATE(:END_DATE, 'YYYY-MM-DD') + 1 OR :END_DATE IS NULL)
   AND (TRIM(A.CONTACT_SN) = :CONTACT_SN OR :CONTACT_SN IS NULL)
   AND (TRIM(A.RSRV_TAG3) = :REG_STATUS)
   AND (TRIM(A.RSRV_STR2) = :SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
   AND (TRIM(A.CUST_NAME) like '%' || :CUST_NAME || '%')
   AND (TRIM(A.SET_ADDR) like '%' || :SET_ADDR || '%')
   AND A.RSRV_STR3 = :AREA_CODE
 ORDER BY A.REG_DATE ASC
 