UPDATE tf_f_user_grpmbmp_info a
   SET end_date=(SELECT start_date-1/24/3600 FROM tf_b_trade_grpmbmp_info b
                  WHERE trade_id = :TRADE_ID
                    AND user_id = TO_NUMBER(a.user_id)
                    and biz_code = a.biz_code
                    and sysdate < a.end_date)
 WHERE exists (select 1 from tf_b_trade_grpmbmp_info where
 a.user_id=user_id
 and a.partition_id=MOD(user_id,10000)
   AND a.biz_code||''=biz_code
   and trade_id = :TRADE_ID
   and oper_code <> '01')   AND sysdate < a.end_date+0