SELECT COUNT(1) recordcount
FROM   tf_b_trade_res
WHERE  trade_id = TO_NUMBER(:TRADE_ID)
and    accept_month = TO_NUMBER(substr(:TRADE_ID,5,2))
AND    res_type_code = :RES_TYPE_CODE
AND    modify_tag = :MODIFY_TAG
AND    (res_code LIKE :RES_CODE OR :RES_CODE IS NULL)
AND    (imsi LIKE :RES_INFO1 OR :RES_INFO1 IS NULL)