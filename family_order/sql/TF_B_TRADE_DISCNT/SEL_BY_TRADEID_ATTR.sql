select t.*
  from tf_b_trade_discnt t
 where t.trade_id = :TRADE_ID
   and t.modify_tag = '1'
   and t.discnt_code in (select a.param_code
                           from td_s_commpara a
                          where a.param_attr = '535'
                            and sysdate < a.end_date)