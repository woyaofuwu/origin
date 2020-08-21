select b.PARTITION_ID, to_char(b.USER_ID) USER_ID, b.PRODUCT_ID, b.PACKAGE_ID,to_char(b.INST_ID) INST_ID, to_char(b.CAMPN_ID) CAMPN_ID, 
 to_char(b.USER_ID_A) USER_ID_A, DISCNT_GIFT_ID, A_DISCNT_CODE, DEPOSIT_TYPE, b.MONTHS MONTHS, to_char(LIMIT_MONEY) LIMIT_MONEY, PAY_MODE, to_char(FEE) FEE, IN_DEPOSIT_CODE, OUT_DEPOSIT_CODE, to_char(b.RELATION_TRADE_ID) RELATION_TRADE_ID, to_char(b.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(b.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_char(b.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, b.UPDATE_STAFF_ID, b.UPDATE_DEPART_ID, b.REMARK, b.RSRV_NUM1, b.RSRV_NUM2, b.RSRV_NUM3, to_char(b.RSRV_NUM4) RSRV_NUM4, to_char(b.RSRV_NUM5) RSRV_NUM5, b.RSRV_STR1, b.RSRV_STR2, b.RSRV_STR3, b.RSRV_STR4, b.RSRV_STR5, to_char(b.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(b.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(b.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, b.RSRV_TAG1, b.RSRV_TAG2, b.RSRV_TAG3
From tf_f_user_sale_active a ,tf_f_user_sale_deposit b
Where a.user_id = b.user_id
And a.package_id =b.package_id
And a.RELATION_TRADE_ID=:RELATION_TRADE_ID
And b.RELATION_TRADE_ID=:RELATION_TRADE_ID
And a.campn_id =:CAMPN_ID
And b.campn_id =:CAMPN_ID
And a.user_id =:USER_ID
And a.partition_id =Mod(:USER_ID,10000)
And b.user_id =:USER_ID
And b.partition_id =Mod(:USER_ID,10000)