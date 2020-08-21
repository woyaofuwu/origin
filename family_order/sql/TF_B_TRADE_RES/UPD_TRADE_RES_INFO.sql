UPDATE tf_b_trade_res
   SET ki=:KI
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND res_type_code = :RES_TYPE_CODE
   AND res_code = :RES_CODE
   AND modify_tag = :MODIFY_TAG