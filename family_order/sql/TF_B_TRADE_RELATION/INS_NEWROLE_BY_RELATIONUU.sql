INSERT INTO TF_B_TRADE_RELATION
  (TRADE_ID, ACCEPT_MONTH, USER_ID_A, SERIAL_NUMBER_A, USER_ID_B,
   SERIAL_NUMBER_B, RELATION_TYPE_CODE, ROLE_TYPE_CODE, ROLE_CODE_A,
   ROLE_CODE_B, ORDERNO, SHORT_CODE, INST_ID, START_DATE, END_DATE,
   MODIFY_TAG, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK,
   RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1,
   RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_DATE1, RSRV_DATE2,
   RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3)
  SELECT TO_NUMBER(:TRADE_ID), TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)),
         USER_ID_A, SERIAL_NUMBER_A, USER_ID_B, SERIAL_NUMBER_B,
         RELATION_TYPE_CODE, ROLE_TYPE_CODE, ROLE_CODE_A, :NEW_ROLE_CODE,
         ORDERNO, SHORT_CODE, :INST_ID,
         TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mi:ss'),
         TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'), :MODIFY_TAG, SYSDATE,
         UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2,
         RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3,
         RSRV_STR4, RSRV_STR5, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1,
         RSRV_TAG2, RSRV_TAG3
    FROM TF_F_RELATION_UU A
   WHERE A.USER_ID_B = TO_NUMBER(:USER_ID_B)
     AND A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE
     AND A.ROLE_CODE_B = :ROLE_CODE_B
     AND SYSDATE < A.END_DATE + 0
     AND NOT EXISTS
   (SELECT 1
            FROM TF_B_TRADE_RELATION C
           WHERE C.TRADE_ID = :TRADE_ID
             AND C.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
             AND C.INST_ID = A.INST_ID
             AND C.USER_ID_B = TO_NUMBER(:USER_ID_B)
             AND C.RELATION_TYPE_CODE = A.RELATION_TYPE_CODE
             AND C.USER_ID_A = A.USER_ID_A
             AND C.MODIFY_TAG = :MODIFY_TAG)