UPDATE TF_F_USER_SALE_DEPOSIT SET 
USER_ID_A = to_number(:USER_ID_A), A_DISCNT_CODE = :A_DISCNT_CODE, DEPOSIT_TYPE = :DEPOSIT_TYPE, INST_ID = to_number(:INST_ID), CAMPN_ID = to_number(:CAMPN_ID), MONTHS = :MONTHS, LIMIT_MONEY = to_number(:LIMIT_MONEY), PAY_MODE = :PAY_MODE, FEE = to_number(:FEE), PAYMENT_ID = :PAYMENT_ID, IN_DEPOSIT_CODE = :IN_DEPOSIT_CODE, OUT_DEPOSIT_CODE = :OUT_DEPOSIT_CODE, START_DATE = to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'), END_DATE = to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'), UPDATE_TIME = to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss'), UPDATE_STAFF_ID = :UPDATE_STAFF_ID, UPDATE_DEPART_ID = :UPDATE_DEPART_ID, REMARK = :REMARK, RSRV_NUM1 = :RSRV_NUM1, RSRV_NUM2 = :RSRV_NUM2, RSRV_NUM3 = :RSRV_NUM3, RSRV_NUM4 = to_number(:RSRV_NUM4), RSRV_NUM5 = to_number(:RSRV_NUM5), RSRV_STR1 = :RSRV_STR1, RSRV_STR2 = :RSRV_STR2, RSRV_STR3 = :RSRV_STR3, RSRV_STR4 = :RSRV_STR4, RSRV_STR5 = :RSRV_STR5, RSRV_DATE1 = to_date(:RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss'), RSRV_DATE2 = to_date(:RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss'), RSRV_DATE3 = to_date(:RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss'), RSRV_TAG1 = :RSRV_TAG1, RSRV_TAG2 = :RSRV_TAG2, RSRV_TAG3 = :RSRV_TAG3 WHERE PARTITION_ID = MOD(to_number(:USER_ID),10000) and USER_ID = to_number(:USER_ID) and PRODUCT_ID = :PRODUCT_ID and PACKAGE_ID = :PACKAGE_ID and DISCNT_GIFT_ID = :DISCNT_GIFT_ID and RELATION_TRADE_ID = to_number(:RELATION_TRADE_ID)