INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,discnt_code,spec_tag,relation_type_code,start_date,end_date,update_time)
select a.partition_id,a.user_id,a.user_id_a,:DISCNT_CODE,a.spec_tag,a.relation_type_code,trunc(to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')),to_date('20501231','yyyymmdd'),sysdate
from tf_f_user_discnt a
where a.user_id=:USER_ID
  and a.spec_tag='2'
  and a.relation_type_code=:RELATION_TYPE_CODE
  and sysdate<a.end_date
  and exists(select 1 from tf_b_trade_discnt b
              where a.user_id=b.id and b.trade_id=:TRADE_ID
                and b.accept_month=to_number(substrb(:TRADE_ID,5,2))
                and b.modify_tag='1' and b.discnt_code=a.discnt_code)
  and rownum<2