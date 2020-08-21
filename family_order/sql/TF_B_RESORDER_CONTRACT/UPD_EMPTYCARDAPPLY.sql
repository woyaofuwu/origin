UPDATE tf_b_resorder_contract
   SET rsrv_str2=:RSRV_STR2,assign_num=TO_NUMBER(:ASSIGN_NUM),remark2=:REMARK2,back_tag=:BACK_TAG 
 WHERE contract_id=:CONTRACT_ID
   AND batch_id=TO_NUMBER(:BATCH_ID)