INSERT INTO TF_B_TRADE_RELATION(TRADE_ID,ACCEPT_MONTH,USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,INST_ID,START_DATE,END_DATE,MODIFY_TAG,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3)
SELECT :TRADE_ID,TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)),USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,INST_ID,START_DATE,TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),:MODIFY_TAG,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 
FROM TF_F_RELATION_UU A
   WHERE A.user_id_a=TO_NUMBER(:USER_ID_A)
   AND A.end_date>A.start_date
   AND (A.role_code_b=:ROLE_CODE_B OR :ROLE_CODE_B='*')
   AND A.end_date=(SELECT MAX(b.end_date) From tf_f_relation_uu b WHERE b.user_id_a=TO_NUMBER(:USER_ID_A) AND b.end_date<SYSDATE)
   AND NOT EXISTS 
   (SELECT 1 FROM TF_B_TRADE_RELATION C
           WHERE C.TRADE_ID = :TRADE_ID
             AND C.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
             AND C.INST_ID = A.INST_ID
             AND C.USER_ID_A = TO_NUMBER(:USER_ID_A)
             AND C.relation_type_code = A.relation_type_code
             AND C.USER_ID_B = A.USER_ID_B)