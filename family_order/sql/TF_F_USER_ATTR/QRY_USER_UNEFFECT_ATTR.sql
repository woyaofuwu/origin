SELECT A.PARTITION_ID,
       A.USER_ID,
       A.INST_TYPE,
       A.INST_ID,
       A.ATTR_CODE,
       A.ATTR_VALUE,
       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') AS START_DATE,
       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') AS END_DATE,
       TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') AS UPDATE_TIME,
       A.UPDATE_STAFF_ID,
       A.UPDATE_DEPART_ID,
       A.REMARK,
       A.RSRV_NUM1,
       A.RSRV_NUM2,
       A.RSRV_NUM3,
       A.RSRV_NUM4,
       A.RSRV_NUM5,
       A.RSRV_STR1,
       A.RSRV_STR2,
       A.RSRV_STR3,
       A.RSRV_STR4,
       A.RSRV_STR5,
       A.RSRV_DATE1,
       A.RSRV_DATE2,
       A.RSRV_DATE3,
       A.RSRV_TAG1,
       A.RSRV_TAG2,
       A.RSRV_TAG3,
       A.RELA_INST_ID,
       A.ELEMENT_ID
  FROM TF_F_USER_ATTR A
 WHERE A.USER_ID = :USER_ID
   AND A.PARTITION_ID = MOD(:USER_ID, 10000)
   AND A.START_DATE > SYSDATE
   AND A.END_DATE > A.START_DATE
   AND EXISTS (SELECT 1
          FROM TD_B_ATTR_ITEMB B
         WHERE B.ID = :ITEM_ID
           AND B.ID_TYPE = :ID_TYPE
           AND B.ATTR_CODE = :ATTR_CODE
           AND B.ATTR_CODE = A.ATTR_CODE
           AND B.ID_TYPE = A.INST_TYPE
           AND B.ATTR_FIELD_CODE = A.ATTR_VALUE
           AND B.START_DATE <= SYSDATE
           AND B.END_DATE >= SYSDATE)