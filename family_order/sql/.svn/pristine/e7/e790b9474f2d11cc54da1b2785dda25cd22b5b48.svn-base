select b.PARTITION_ID, to_char(b.USER_ID) USER_ID, to_char(b.USER_ID_A) USER_ID_A, a.PRODUCT_ID, a.PACKAGE_ID, b.discnt_code, b.spec_tag,b.relation_type_code,
 to_char(b.INST_ID) INST_ID, to_char(b.CAMPN_ID) CAMPN_ID, to_char(b.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, 
to_char(b.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_char(b.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, b.UPDATE_STAFF_ID, 
b.UPDATE_DEPART_ID, b.REMARK, b.RSRV_STR1
From tf_f_user_sale_active a ,tf_f_user_discnt b
Where a.user_id = b.user_id
And a.relation_trade_id = b.rsrv_str1
And a.RELATION_TRADE_ID=:RELATION_TRADE_ID
And a.campn_id =:CAMPN_ID
And b.campn_id =:CAMPN_ID
And a.user_id =:USER_ID
And a.partition_id =Mod(:USER_ID,10000)
And b.user_id =:USER_ID
And b.partition_id =Mod(:USER_ID,10000)