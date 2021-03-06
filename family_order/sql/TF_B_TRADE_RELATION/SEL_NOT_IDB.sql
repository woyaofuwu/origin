SELECT TRADE_ID, ACCEPT_MONTH, USER_ID_A, SERIAL_NUMBER_A, USER_ID_B,
       SERIAL_NUMBER_B, RELATION_TYPE_CODE, ROLE_TYPE_CODE, ROLE_CODE_A,
       ROLE_CODE_B, ORDERNO, SHORT_CODE, INST_ID, START_DATE, END_DATE,
       MODIFY_TAG, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK,
       RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1,
       RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_DATE1, RSRV_DATE2,
       RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3
  FROM TF_B_TRADE_RELATION
 WHERE TRADE_ID = :TRADE_ID
   AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
   AND RELATION_TYPE_CODE IN (:RELATION_TYPE_CODE1, :RELATION_TYPE_CODE2)
   AND USER_ID_B <> :USER_ID_B
   AND MODIFY_TAG = :MODIFY_TAG