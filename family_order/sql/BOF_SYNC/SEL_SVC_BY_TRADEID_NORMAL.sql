SELECT :SYNC_SEQUENCE SYNC_SEQUENCE, :SYNC_DAY SYNC_DAY,
               DECODE(B.MODIFY_TAG, '0', '0', '1', '2', '2', '2', '2') MODIFY_TAG,
               B.TRADE_ID, MOD(B.USER_ID, 10000) PARTITION_ID, B.USER_ID, B.USER_ID_A,
               B.SERVICE_ID, B.MAIN_TAG, B.INST_ID,
               B.CAMPN_ID, B.START_DATE, B.END_DATE, B.UPDATE_TIME,
               B.UPDATE_STAFF_ID, B.UPDATE_DEPART_ID, B.REMARK, B.RSRV_NUM1,
               B.RSRV_NUM2, B.RSRV_NUM3, B.RSRV_NUM4, B.RSRV_NUM5, B.RSRV_STR1,
               B.RSRV_STR2, B.RSRV_STR3, B.RSRV_STR4, B.RSRV_STR5, B.RSRV_STR6,
               B.RSRV_STR7, B.RSRV_STR8, B.RSRV_STR9, B.RSRV_STR10, B.RSRV_DATE1,
               B.RSRV_DATE2, B.RSRV_DATE3, B.RSRV_TAG1, B.RSRV_TAG2, B.RSRV_TAG3
          FROM TF_B_TRADE_SVC B
         WHERE B.TRADE_ID = :TRADE_ID
           AND B.ACCEPT_MONTH = :ACCEPT_MONTH
           AND B.MODIFY_TAG IN ('0', '1', '2', 'U')