INSERT INTO tf_a_payrelation(partition_id,user_id,acct_id,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,act_tag,limit_type,limit,complement_tag,update_staff_id,update_depart_id,update_time)
select mod(user_id,10000),user_id,acct_id,payitem_code,acct_priority,user_priority,bind_type,
       start_cycle_id,end_cycle_id,default_tag,'1',limit_type,limit,complement_tag,
       :TRADE_STAFF_ID,:TRADE_DEPART_ID,sysdate
from tf_b_trade_payrelation
where trade_id=to_number(:TRADE_ID)