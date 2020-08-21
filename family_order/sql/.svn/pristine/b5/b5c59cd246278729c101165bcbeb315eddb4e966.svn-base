UPDATE tf_b_resorder_contract
   SET rsrv_num1= rsrv_num1-to_number(:RSRV_NUM1), rsrv_num2=rsrv_num2+to_number(:RSRV_NUM1), 
   back_tag=decode(rsrv_num1-to_number(:RSRV_NUM1),0,'1',back_tag)
 WHERE contract_id=:CONTRACT_ID
   AND batch_id=TO_NUMBER(:BATCH_ID)
   AND res_type_code=:RES_TYPE_CODE
   AND (:BACK_TAG is null or back_tag=:BACK_TAG)