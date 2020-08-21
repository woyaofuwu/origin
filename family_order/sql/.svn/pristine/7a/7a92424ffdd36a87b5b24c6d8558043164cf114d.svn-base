select b.trade_id,
       b.accept_month,
       b.user_id,
       b.serial_number,
       b.SCORE_TYPE_CODE,
       b.score,
       b.score_changed,
       b.value_changed,
       b.score_tag,
       b.rule_id,
       b.action_count,
       b.res_id,
       b.goods_name,
       b.cancel_tag,
       b.remark,
       c.trade_type_code trade_type
  from tf_b_trade_score b,
   tf_b_trade c,
  (select user_id from ucr_crm1.tf_f_user where serial_number = :SERIAL_NUMBER
                                  and remove_tag = :REMOVE_TAG) d
      
 where b.user_id = d.user_id
   and b.trade_id = c.trade_id
   AND b.accept_month = c.accept_month
   and (c.trade_type_code =:TRADE_TYPE_CODE  or :TRADE_TYPE_CODE is null)
   AND c.subscribe_state = '9'
   AND c.cancel_tag ='0'
   AND c.accept_date between to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS') AND to_date(:END_DATE,'YYYY-MM-DD HH24:MI:SS')+1
UNION ALL
select b.trade_id,
       b.accept_month,
       b.user_id,
       b.serial_number,
       b.SCORE_TYPE_CODE,
       b.score,
       b.score_changed,
       b.value_changed,
       b.score_tag,
       b.rule_id,
       b.action_count,
       b.res_id,
       b.goods_name,
       b.cancel_tag,
       b.remark,
       c.trade_type_code trade_type
  from tf_b_trade_score b,
       tf_bh_trade c,
       (select user_id from ucr_crm1.tf_f_user where serial_number = :SERIAL_NUMBER
                                  and remove_tag = :REMOVE_TAG) d
 where b.user_id = d.user_id
   and b.trade_id = c.trade_id
   AND b.accept_month = c.accept_month
   and (c.trade_type_code =:TRADE_TYPE_CODE  or :TRADE_TYPE_CODE is null)
   AND c.cancel_tag ='0'
   AND c.accept_date between to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS') AND to_date(:END_DATE,'YYYY-MM-DD HH24:MI:SS')+1