SELECT C.SEQ_ID,
       C.TRADE_ID,
       C.SERIAL_NUMBER,
       C.CLUB_TYPE,
       C.AGREEMENT_TAG,
       to_char(C.ENTRY_TIME, ' yyyy - mm - dd hh24 :mi :ss
   ') ENTRY_TIME,
       C.IN_MODE_CODE,
       C.RELATED_ACTIVITY_ID,
       C.ACCEPT_TAG,
       C.REASON,
       to_char(C.SINGNING_TIME, ' yyyy - mm - dd hh24 :mi :ss
   ') SINGNING_TIME,
       C.RSRV_STR1,
       C.RSRV_STR2,
       C.RSRV_STR3,
       C.RSRV_STR4,
       C.CUST_NAME,
       to_char(C.UPDATE_TIME, ' yyyy - mm - dd hh24 :mi :ss
   ') UPDATE_TIME,
       C.TRADE_STAFF_ID
  FROM TF_O_CUSTOMERCLUB C
 WHERE C.SERIAL_NUMBER = :SERIAL_NUMBER