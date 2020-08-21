SELECT PARTITION_ID,
       TO_CHAR(USER_ID) USER_ID,
       TO_CHAR(USER_ID_A) USER_ID_A,
       DISCNT_CODE,
       SPEC_TAG,
       RELATION_TYPE_CODE,
       TO_CHAR(RELA_INST_ID) RELA_INST_ID,
       TO_CHAR(INST_ID) INST_ID,
       TO_CHAR(CAMPN_ID) CAMPN_ID,
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       TO_CHAR(RSRV_NUM4) RSRV_NUM4,
       TO_CHAR(RSRV_NUM5) RSRV_NUM5,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3,
       SERVICE_ID
  FROM TF_F_USER_DISCNT
 WHERE (USER_ID, DISCNT_CODE, INST_ID) IN
       (SELECT A.USER_ID_A, A.ELEMENT_ID, A.INST_ID
          FROM TF_F_RELATION_XXT A
         WHERE A.USER_ID_A = :USER_ID_A
           AND A.SERIAL_NUMBER_B = :SERIAL_NUMBER_B
           AND A.ELEMENT_TYPE_CODE = 'D'
           AND A.EC_USER_ID = :EC_USER_ID
           AND A.END_DATE >=
               TO_DATE(TO_CHAR(SYSDATE, 'yyyymm') || '01', 'yyyymmdd')
           AND A.END_DATE < SYSDATE
           AND A.RSRV_STR2 = :GROUP
           AND A.END_DATE IN
               (SELECT MAX(B.END_DATE)
                  FROM TF_F_RELATION_XXT B
                 WHERE B.USER_ID_A = :USER_ID_A
                   AND B.SERIAL_NUMBER_B = :SERIAL_NUMBER_B
                   AND B.ELEMENT_TYPE_CODE = 'D'
                   AND B.EC_USER_ID = :EC_USER_ID
                   AND B.END_DATE >=
                       TO_DATE(TO_CHAR(SYSDATE, 'yyyymm') || '01', 'yyyymmdd')
                   AND B.END_DATE < SYSDATE
                   AND B.RSRV_STR2 = :GROUP
                   AND NOT EXISTS
                 (SELECT 1
                          FROM TF_F_RELATION_XXT C
                         WHERE C.USER_ID_A = :USER_ID_A
                           AND C.SERIAL_NUMBER_B = :SERIAL_NUMBER_B
                           AND C.ELEMENT_TYPE_CODE = 'D'
                           AND C.EC_USER_ID = :EC_USER_ID
                           AND C.START_DATE <= SYSDATE
                           AND C.END_DATE > SYSDATE
                           AND C.RSRV_STR2 = :GROUP)))
