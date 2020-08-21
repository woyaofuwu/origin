SELECT sum(assign_num) assign_num,sum(sale_money) sale_money,sum(rsrv_tag1) rsrv_tag1
  FROM tf_b_resorder_contract
 WHERE contract_id=:CONTRACT_ID
   AND (:BATCH_ID is null or batch_id=TO_NUMBER(:BATCH_ID))
   AND (:RES_TYPE_CODE is null OR res_type_code=:RES_TYPE_CODE)
   AND (:BACK_TAG is null OR back_tag=:BACK_TAG)