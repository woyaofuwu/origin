delete from tf_f_relation_uu a
where exists (select 1 from tf_b_trade_relation
              where trade_id=to_number(:TRADE_ID)
                and accept_month=to_number(substrb(:TRADE_ID,5,2))
                and relation_type_code=a.relation_type_code
                and id_a=a.user_id_a
                and id_b=a.user_id_b
                and start_date=a.start_date)