INSERT INTO TI_B_USER_GRP_CENPAY
      (SYNC_SEQUENCE, SYNC_DAY, MODIFY_TAG, TRADE_ID, PARTITION_ID, USER_ID,
       INST_ID, MP_GROUP_CUST_CODE, GROUP_ID, BIZ_MODE, PROVINCE_CODE, CUST_NAME,
       MERCH_SPEC_CODE, PRODUCT_SPEC_CODE, PRODUCT_ID, PRODUCT_OFFER_ID,
       PAY_TYPE, OPER_TYPE, SERVICE_ID, LIMIT_FEE, START_DATE, END_DATE,
       UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1,
       RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2,
       RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3,
       RSRV_TAG1, RSRV_TAG2, RSRV_TAG3)
      SELECT :SYNC_SEQUENCE, :SYNC_DAY,
             DECODE(B.MODIFY_TAG, '0', '0', '1', '2', '2', '2', '2'), B.TRADE_ID,
             MOD(B.USER_ID, 10000), B.USER_ID, B.INST_ID, B.MP_GROUP_CUST_CODE,
             B.GROUP_ID, B.BIZ_MODE, B.PROVINCE_CODE, B.CUST_NAME,
             B.MERCH_SPEC_CODE, B.PRODUCT_SPEC_CODE, B.PRODUCT_ID,
             B.PRODUCT_OFFER_ID, B.PAY_TYPE, B.OPER_TYPE, B.SERVICE_ID,
             B.LIMIT_FEE, B.START_DATE, B.END_DATE, B.UPDATE_TIME,
             B.UPDATE_STAFF_ID, B.UPDATE_DEPART_ID, B.REMARK, B.RSRV_NUM1,
             B.RSRV_NUM2, B.RSRV_NUM3, B.RSRV_NUM4, B.RSRV_NUM5, B.RSRV_STR1,
             B.RSRV_STR2, B.RSRV_STR3, B.RSRV_STR4, B.RSRV_STR5, B.RSRV_DATE1,
             B.RSRV_DATE2, B.RSRV_DATE3, B.RSRV_TAG1, B.RSRV_TAG2, B.RSRV_TAG3
        FROM TF_B_TRADE_GRP_CENPAY B
       WHERE B.TRADE_ID = :TRADE_ID
         AND B.ACCEPT_MONTH = :ACCEPT_MONTH