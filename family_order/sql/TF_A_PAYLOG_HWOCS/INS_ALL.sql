INSERT INTO tf_a_paylog_hwocs(charge_id,partition_id,acct_id,deposit_code,recv_fee,recv_time,origin_no,update_time,operate_flag)
 VALUES(TO_NUMBER(:CHARGE_ID),:PARTITION_ID,TO_NUMBER(:ACCT_ID),:DEPOSIT_CODE,TO_NUMBER(:RECV_FEE),TO_DATE(:RECV_TIME,'YYYY-MM-DD HH24:MI:SS'),TO_NUMBER(:ORIGIN_NO),TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS'),:OPERATE_FLAG)