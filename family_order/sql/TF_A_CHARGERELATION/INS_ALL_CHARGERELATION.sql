INSERT INTO tf_a_chargerelation(operate_id1,operate_type1,operate_id2,operate_type2,relation_type,update_time,partition_id,eparchy_code,trade_id)
 VALUES(TO_NUMBER(:OPERATE_ID1),:OPERATE_TYPE1,TO_NUMBER(:OPERATE_ID2),:OPERATE_TYPE2,:RELATION_TYPE,TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS'),:PARTITION_ID,:EPARCHY_CODE,TO_NUMBER(:TRADE_ID))