UPDATE tf_b_trade_plat_register a
   SET remark=:REMARK,oper_code=:OPER_CODE  
 WHERE partition_id=MOD(TO_NUMBER(:TRADE_ID), 10000)
   AND trade_id=TO_NUMBER(:TRADE_ID)
   AND oper_code IN ('06','11')
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_plat_register 
                WHERE partition_id = MOD(a.User_Id,10000)
                  AND user_id = a.user_id
                  AND biz_type_code = a.biz_type_code
                  AND SYSDATE BETWEEN start_date+0 AND end_date+0)