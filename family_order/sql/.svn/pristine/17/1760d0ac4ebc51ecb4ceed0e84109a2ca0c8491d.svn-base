UPDATE tf_f_user_plat_register a
   SET end_date=(SELECT start_date-1/24/3600 FROM tf_b_trade_plat_register b
                  WHERE partition_id = MOD(:TRADE_ID,10000)
                    AND trade_id = :TRADE_ID
                    AND user_id = TO_NUMBER(a.user_id)
                    and biz_type_code = a.biz_type_code
                    and (rsrv_num1 = to_number(:RSRV_NUM1) or :RSRV_NUM1 is null)
                    and oper_code NOT IN ('04','05','06','07','01','11','14','15'))
 WHERE partition_id = MOD(:USER_ID,10000)
   AND user_id = :USER_ID
   AND exists (select 1 
                 from tf_b_trade_plat_register 
                where partition_id = MOD(:TRADE_ID,10000)
                  AND trade_id = :TRADE_ID
                  AND a.user_id=user_id
                  AND a.biz_type_code = biz_type_code
                  AND sysdate between a.start_date and a.end_date
                  and (rsrv_num1 = to_number(:RSRV_NUM1) or :RSRV_NUM1 is null)
                  and oper_code NOT IN ('04','05','06','07','01','11','14','15'))