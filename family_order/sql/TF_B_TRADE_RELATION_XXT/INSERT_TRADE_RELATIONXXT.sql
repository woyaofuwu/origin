INSERT INTO TF_B_TRADE_RELATION_XXT
  (TRADE_ID,
   ACCEPT_MONTH,
   USER_ID_A,
   SERIAL_NUMBER_A,
   SERIAL_NUMBER_B,
   ELEMENT_TYPE_CODE,
   ELEMENT_ID,
   INST_ID,
   START_DATE,
   END_DATE,
   NAME,
   EC_USER_ID,
   UPDATE_TIME,
   UPDATE_STAFF_ID,
   UPDATE_DEPART_ID,
   REMARK,
   RSRV_NUM1,
   RSRV_NUM2,
   RSRV_NUM3,
   RSRV_STR1,
   RSRV_STR2,
   RSRV_STR3,
   RSRV_STR4,
   RSRV_STR5,
   RSRV_STR6,
   RSRV_STR7,
   RSRV_STR8,
   RSRV_STR9,
   RSRV_STR10,
   RSRV_DATE1,
   RSRV_DATE2,
   RSRV_DATE3,
   MODIFY_TAG)
VALUES
  (TO_NUMBER(:TRADE_ID),
   TO_NUMBER(:ACCEPT_MONTH),
   TO_NUMBER(:USER_ID_A),
   :SERIAL_NUMBER_A,
   :SERIAL_NUMBER_B,
   :ELEMENT_TYPE_CODE,
   TO_NUMBER(:ELEMENT_ID),
   TO_NUMBER(:INST_ID),
   TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mi:ss'),
   TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'),
   :NAME,
   TO_NUMBER(:EC_USER_ID),
   TO_DATE(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss'),
   :UPDATE_STAFF_ID,
   :UPDATE_DEPART_ID,
   :REMARK,
   TO_NUMBER(:RSRV_NUM1),
   TO_NUMBER(:RSRV_NUM2),
   TO_NUMBER(:RSRV_NUM3),
   :RSRV_STR1,
   :RSRV_STR2,
   :RSRV_STR3,
   :RSRV_STR4,
   :RSRV_STR5,
   :RSRV_STR6,
   :RSRV_STR7,
   :RSRV_STR8,
   :RSRV_STR9,
   :RSRV_STR10,
   TO_DATE(:RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss'),
   TO_DATE(:RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss'),
   TO_DATE(:RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss'),
   :MODIFY_TAG)