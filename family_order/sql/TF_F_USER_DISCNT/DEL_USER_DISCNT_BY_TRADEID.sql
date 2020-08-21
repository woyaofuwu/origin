delete from tf_f_user_discnt a
where exists (select 1 from tf_b_trade_discnt
              where trade_id=to_number(:TRADE_ID)
                and accept_month=to_number(substrb(:TRADE_ID,5,2))
                and id=a.user_id
                and id_type='1'
                and discnt_code=a.discnt_code)
  and user_id=to_number(:USER_ID)
  and partition_id=mod(to_number(:USER_ID),10000)
  and end_date>sysdate