INSERT INTO tf_a_adjust_hwocs(adjust_id,acct_id,user_id,adjust_time,adjust_type,item_type,item_code,adjust_fee,bcyc_id,update_time,operate_flag)
 VALUES(TO_NUMBER(:ADJUST_ID),TO_NUMBER(:ACCT_ID),TO_NUMBER(:USER_ID),TO_DATE(:ADJUST_TIME,'YYYY-MM-DD HH24:MI:SS'),:ADJUST_TYPE,:ITEM_TYPE,:ITEM_CODE,TO_NUMBER(:ADJUST_FEE),:BCYC_ID,TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS'),:OPERATE_FLAG)