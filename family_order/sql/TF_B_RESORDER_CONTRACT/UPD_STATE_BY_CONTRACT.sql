UPDATE tf_b_resorder_contract
   SET back_tag=:BACK_TAG_NEW
 WHERE contract_id=:CONTRACT_ID
   AND (:BATCH_ID is null or batch_id=TO_NUMBER(:BATCH_ID))
   AND res_type_code=:RES_TYPE_CODE
   AND (:BACK_TAG is null or back_tag=:BACK_TAG)