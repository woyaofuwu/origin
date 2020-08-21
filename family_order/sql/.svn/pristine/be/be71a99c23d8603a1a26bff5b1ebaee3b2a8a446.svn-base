INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
select to_number(:TRADE_ID),to_number(substrb(:TRADE_ID,5,2)),user_id,'1',discnt_code,'1',start_date,TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS') 
from tf_f_user_discnt a
where partition_id=mod(to_number(:USER_ID),10000)
  and user_id=to_number(:USER_ID)
  and start_date+0<end_date
  and end_date+0>sysdate
  and exists (select 1 from td_b_discnt
              where discnt_code=a.discnt_code
                and discnt_type_code='5')