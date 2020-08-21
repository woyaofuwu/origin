INSERT INTO TF_F_USER_ATTR
  (PARTITION_ID,
   USER_ID,
   INST_TYPE,
   INST_ID,
   ATTR_CODE,
   ATTR_VALUE,
   START_DATE,
   END_DATE,
   UPDATE_TIME,
   UPDATE_STAFF_ID,
   UPDATE_DEPART_ID,
   REMARK,
   RSRV_NUM1,
   RSRV_NUM2,
   RSRV_NUM3,
   RSRV_NUM4,
   RSRV_NUM5,
   RSRV_STR1,
   RSRV_STR2,
   RSRV_STR3,
   RSRV_STR4,
   RSRV_STR5,
   RSRV_DATE1,
   RSRV_DATE2,
   RSRV_DATE3,
   RSRV_TAG1,
   RSRV_TAG2,
   RSRV_TAG3)
  SELECT MOD(:USER_ID, 10000),
         :USER_ID,
         C.ID_TYPE,
         :INST_ID,
         C.ATTR_CODE,
         C.ATTR_INIT_VALUE,
         C.START_DATE,
         C.END_DATE,
         SYSDATE,
         C.UPDATE_STAFF_ID,
         C.UPDATE_DEPART_ID,
         C.REMARK,
         C.ID,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL
        
    FROM TD_B_PRODUCT_PACKAGE A, TD_B_PACKAGE_ELEMENT B, TD_B_ATTR_ITEMA C
   WHERE A.PRODUCT_ID = :PRODUCT_ID
    AND   A.EPARCHY_CODE in(:EPARCHY_CODE,'ZZZZ')
     AND A.PACKAGE_ID = B.PACKAGE_ID
     AND A.FORCE_TAG = '1'
     AND B.FORCE_TAG = '1'
     AND C.ID_TYPE = B.ELEMENT_TYPE_CODE
     AND C.ID = B.ELEMENT_ID
     AND C.EPARCHY_CODE=A.EPARCHY_CODE
     AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE
     AND SYSDATE BETWEEN B.START_DATE AND B.END_DATE
     AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE