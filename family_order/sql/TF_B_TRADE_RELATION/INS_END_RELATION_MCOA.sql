INSERT INTO TF_B_TRADE_RELATION(TRADE_ID,ACCEPT_MONTH,USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,INST_ID,START_DATE,END_DATE,MODIFY_TAG,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3)
SELECT :TRADE_ID,SUBSTR(:TRADE_ID, 5, 2),USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,INST_ID,START_DATE,TO_DATE(TO_CHAR(last_day(add_months(SYSDATE,-1)), 'YYYY-MM-DD') ||' 23:59:59','YYYY-MM-DD HH24:MI:SS'), :MODIFY_TAG,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 
FROM TF_F_RELATION_UU
   WHERE USER_ID_A = TO_NUMBER(:USER_ID_A)
     AND RELATION_TYPE_CODE = '56'
     AND END_DATE > SYSDATE