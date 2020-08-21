SELECT sum(assign_num) assign_num,
    SUM(RSRV_NUM1) rsrv_num1,
        sum(rsrv_num2) rsrv_num2
FROM tf_b_resorder_contract
WHERE res_type_code=:RES_TYPE_CODE
  AND contract_id=:CONTRACT_ID
  AND (:BATCH_ID IS NULL OR batch_id=TO_number(:BATCH_ID))
  AND (:BACK_TAG IS NULL OR back_tag=:BACK_TAG)