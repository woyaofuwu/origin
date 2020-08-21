select b.PARTITION_ID, to_char(b.USER_ID) USER_ID, b.PRODUCT_ID, b.PACKAGE_ID,to_char(b.INST_ID) INST_ID, to_char(b.CAMPN_ID) CAMPN_ID, 
b.SERIAL_NUMBER_B,  GOODS_ID, GOODS_NAME, to_char(GOODS_NUM) GOODS_NUM, to_char(GOODS_VALUE) GOODS_VALUE, GOODS_STATE, RES_TAG, RES_TYPE_CODE, RES_ID, RES_CODE, DEVICE_MODEL_CODE, DEVICE_MODEL, to_char(DEVICE_COST) DEVICE_COST,DEVICE_BRAND_CODE, DEVICE_BRAND, DESTROY_FLAG, GIFT_MODE,   POST_NAME, POST_ADDRESS, POST_CODE,  to_char(b.RELATION_TRADE_ID) RELATION_TRADE_ID,to_char(b.CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,to_char(b.ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE
From tf_f_user_sale_active a ,tf_f_user_sale_goods b
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