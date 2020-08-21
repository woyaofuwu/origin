INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
select to_number(:TRADE_ID),to_number(substrb(:TRADE_ID,5,2)),
    to_number(:USER_ID),'1',discnt_code,'1',start_date,TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')
from tf_f_user_discnt
 where user_id=to_number(:USER_ID)
  and partition_id=mod(to_number(:USER_ID),10000)
  and start_date<end_date
  and end_date>sysdate
  and user_id_a=to_number(:USER_ID_A)
  and end_date>TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')