SELECT contract_id,to_char(batch_id) batch_id 
  FROM tf_b_resorder_contract
 WHERE res_type_code||''=:RES_TYPE_CODE
   AND (:BACK_TAG IS NULL OR back_tag=:BACK_TAG)
 ORDER BY contract_id, batch_id