INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
select to_number(:TRADE_ID),to_number(substrb(:TRADE_ID,5,2)),to_number(:USER_ID),'1',discnt_code,'0',TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),end_date
from tf_f_user_discnt a
where spec_tag='0' and user_id_a=-1
 and start_date<end_date
 and end_date>sysdate
 and user_id=to_number(:USER_ID_A)
 and partition_id=mod(to_number(:USER_ID_A),10000)
 and not exists (select 1 from tf_f_user_discnt
                 where user_id=to_number(:USER_ID)
                  and partition_id=mod(to_number(:USER_ID),10000)
                  and discnt_code=a.discnt_code
                  and start_date<end_date
                  and end_date>sysdate
                  and user_id_a=to_number(:USER_ID_A))