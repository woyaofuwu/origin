INSERT INTO TI_B_BANK_MAINSIGN
      (SYNC_SEQUENCE, SYNC_DAY, MODIFY_TAG, TRADE_ID, SIGN_ID, USER_TYPE,
       USER_VALUE, HOME_AREA, BANK_ACCT_ID, BANK_ACCT_TYPE, BANK_ID, CHNL_TYPE,
       PAY_TYPE, RECH_THRESHOLD, RECH_AMOUNT, SIGN_STATE, INST_ID, APPLY_DATE,
       START_DATE, END_DATE, USER_NAME, ID_TYPE, ID_VALUE, UPDATE_DATE,
       UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, RSRV_STR2,
       RSRV_STR3, RSRV_STR4, RSRV_STR5)
      SELECT :SYNC_SEQUENCE, :SYNC_DAY,
             DECODE(B.MODIFY_TAG, '0', '0', '1', '2', '2', '2', '2'), B.TRADE_ID,
             B.SIGN_ID, B.USER_TYPE, B.USER_VALUE, B.HOME_AREA, B.BANK_ACCT_ID,
             B.BANK_ACCT_TYPE, B.BANK_ID, B.CHNL_TYPE, B.PAY_TYPE,
             B.RECH_THRESHOLD, B.RECH_AMOUNT, B.SIGN_STATE, B.INST_ID,
             B.APPLY_DATE, B.START_DATE, B.END_DATE, B.USER_NAME, B.ID_TYPE,
             B.ID_VALUE, B.UPDATE_DATE, B.UPDATE_STAFF_ID, B.UPDATE_DEPART_ID,
             B.REMARK, B.RSRV_STR1, B.RSRV_STR2, B.RSRV_STR3, B.RSRV_STR4,
             B.RSRV_STR5
        FROM TF_B_TRADE_BANK_MAINSIGN B
       WHERE B.TRADE_ID = :TRADE_ID
         AND ACCEPT_MONTH = :ACCEPT_MONTH