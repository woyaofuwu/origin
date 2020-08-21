UPDATE tf_f_user_plat_order a
   SET a.end_date=(SELECT start_date-1/24/3600 FROM tf_b_trade_plat_order 
                  WHERE partition_id = MOD(:TRADE_ID,10000)
                    AND trade_id = :TRADE_ID
                    AND user_id = TO_NUMBER(a.user_id)
                    and biz_code = a.biz_code
                    and sp_code = a.sp_code
                    and sysdate between a.start_date and a.end_date
                    and (rsrv_num1 = to_number(:RSRV_NUM1) or :RSRV_NUM1 is null)
                    and oper_code <> '06' 
                    AND oper_code <> '11')
 WHERE a.partition_id = MOD(:USER_ID,10000)
   AND a.user_id = :USER_ID
   AND EXISTS (SELECT 1 FROM tf_b_trade_plat_order 
                WHERE partition_id = MOD(:TRADE_ID,10000)
                 AND trade_id = :TRADE_ID 
                 AND user_id = a.user_id
                 AND biz_code=a.biz_code
                 and sp_code = a.sp_code
                 AND sysdate between a.start_date and a.end_date
                 and (rsrv_num1 = to_number(:RSRV_NUM1) or :RSRV_NUM1 is null)
                 and oper_code <> '06'
                 and oper_code <> '11')