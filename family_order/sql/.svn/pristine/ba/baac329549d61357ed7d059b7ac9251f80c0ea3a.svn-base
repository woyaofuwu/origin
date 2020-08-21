SELECT to_char(B.TRADE_ID) TRADE_ID,
       B.ACCEPT_MONTH,
       to_char(B.USER_ID) USER_ID,
       to_char(B.USER_ID_A) USER_ID_A,
       B.DISCNT_CODE,
       B.SPEC_TAG,
       B.RELATION_TYPE_CODE,
       to_char(B.INST_ID) INST_ID,
       to_char(B.CAMPN_ID) CAMPN_ID,
       to_char(B.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(B.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       B.MODIFY_TAG,
       to_char(B.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       B.UPDATE_STAFF_ID,
       B.UPDATE_DEPART_ID,
       B.REMARK,
       B.RSRV_NUM1,
       B.RSRV_NUM2,
       B.RSRV_NUM3,
       to_char(B.RSRV_NUM4) RSRV_NUM4,
       to_char(B.RSRV_NUM5) RSRV_NUM5,
       B.RSRV_STR1,
       B.RSRV_STR2,
       B.RSRV_STR3,
       B.RSRV_STR4,
       B.RSRV_STR5,
       to_char(B.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(B.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(B.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       B.RSRV_TAG1,
       B.RSRV_TAG2,
       B.RSRV_TAG3,
       A.TRADE_TYPE_CODE
  FROM TF_BH_TRADE A, TF_B_TRADE_DISCNT B
 WHERE 1=1
   AND B.USER_ID = A.USER_ID
   AND B.TRADE_ID = A.TRADE_ID
   AND B.DISCNT_CODE = :DISCNT_CODE
   AND B.USER_ID = :USER_ID
   AND B.ACCEPT_MONTH=A.ACCEPT_MONTH
