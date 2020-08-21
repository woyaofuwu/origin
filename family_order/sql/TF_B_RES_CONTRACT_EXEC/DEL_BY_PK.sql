DELETE FROM tf_b_res_contract_exec
 WHERE contract_id=:CONTRACT_ID
   AND res_type_code=:RES_TYPE_CODE
   AND res_kind_code=:RES_KIND_CODE
   AND oper_num=TO_NUMBER(:OPER_NUM)
   AND device_price=TO_NUMBER(:DEVICE_PRICE)
   AND time_in=TO_DATE(:TIME_IN, 'YYYY-MM-DD HH24:MI:SS')
   AND factory_code=:FACTORY_CODE
   AND area_code=:AREA_CODE