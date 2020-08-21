Insert INTO TF_B_TRADE_SALE_GOODS(TRADE_ID, ACCEPT_MONTH, USER_ID, SERIAL_NUMBER_B, PRODUCT_ID, PACKAGE_ID, INST_ID, CAMPN_ID, GOODS_ID, GOODS_NAME, GOODS_NUM, GOODS_VALUE, GOODS_STATE, RES_TAG, RES_TYPE_CODE, RES_ID, RES_CODE, DEVICE_MODEL_CODE, DEVICE_MODEL, DEVICE_COST, DEVICE_BRAND_CODE, DEVICE_BRAND, DESTROY_FLAG, GIFT_MODE, POST_NAME, POST_ADDRESS, POST_CODE, RELATION_TRADE_ID, ACCEPT_DATE, CANCEL_DATE, MODIFY_TAG, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3) 
VALUES (to_number(:TRADE_ID) , :ACCEPT_MONTH, to_number(:USER_ID) , :SERIAL_NUMBER_B, :PRODUCT_ID, :PACKAGE_ID, to_number(:INST_ID) , to_number(:CAMPN_ID) , :GOODS_ID, :GOODS_NAME, to_number(:GOODS_NUM) , to_number(:GOODS_VALUE) , :GOODS_STATE, :RES_TAG, :RES_TYPE_CODE, :RES_ID, :RES_CODE, :DEVICE_MODEL_CODE, :DEVICE_MODEL, to_number(:DEVICE_COST) , :DEVICE_BRAND_CODE, :DEVICE_BRAND, :DESTROY_FLAG, :GIFT_MODE, :POST_NAME, :POST_ADDRESS, :POST_CODE, to_number(:RELATION_TRADE_ID) , to_date(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') , to_date(:CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') , :MODIFY_TAG, to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') , :UPDATE_STAFF_ID, :UPDATE_DEPART_ID, :REMARK, :RSRV_NUM1, :RSRV_NUM2, :RSRV_NUM3, to_number(:RSRV_NUM4) , to_number(:RSRV_NUM5) , :RSRV_STR1, :RSRV_STR2, :RSRV_STR3, :RSRV_STR4, :RSRV_STR5, :RSRV_STR6, :RSRV_STR7, :RSRV_STR8, :RSRV_STR9, :RSRV_STR10, to_date(:RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') , to_date(:RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') , to_date(:RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') , :RSRV_TAG1, :RSRV_TAG2, :RSRV_TAG3)