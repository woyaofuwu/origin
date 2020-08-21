SELECT distinct contract_id
  FROM tf_b_resorder_contract
 WHERE res_type_code=:RES_TYPE_CODE
   AND (:BACK_TAG is null or back_tag=:BACK_TAG)
  ORDER BY contract_id