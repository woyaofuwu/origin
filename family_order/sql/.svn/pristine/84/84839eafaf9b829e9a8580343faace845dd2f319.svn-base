select to_char(charge_id) charge_id,partition_id,eparchy_code,city_code,to_char(rsrv_info1) cust_id,to_char(user_id) user_id,to_char(acct_id) acct_id,charge_source_code,pay_fee_mode_code,to_char(abs(recv_fee)) recv_fee,deposit_code,cancel_tag cancel_tag,to_char(recv_time,'yyyy-mm-dd hh24:mi:ss') recv_time,recv_eparchy_code,recv_city_code,recv_depart_id,recv_staff_id,to_char(cancel_time,'yyyy-mm-dd hh24:mi:ss') cancel_time,cancel_eparchy_code,cancel_city_code,cancel_depart_id,cancel_staff_id 
from (
select * from tf_a_paylog a
  where 
  a.partition_id >= TO_NUMBER(:PARTITION_ID1 )
  and a.partition_id <=TO_NUMBER( :PARTITION_ID2)
  AND a.user_id = TO_NUMBER(:USER_ID) 
  AND a.charge_source_code =:CHARGE_SOURCE_CODE
  AND a.pay_fee_mode_code=4
  AND ( recv_time>= TO_DATE(:BEGIN_TIME, 'YYYY-MM-DD') or :BEGIN_TIME is null)
  AND (recv_time <= TO_DATE(:END_TIME, 'YYYY-MM-DD')+1   or :END_TIME is null)
  AND a.cancel_tag=0
  order by recv_time desc) c ,(
select rsrv_info1,partition_id partition_id1,operate_id
from tf_a_writesnap_log 
where  
partition_id >= TO_NUMBER(:PARTITION_ID1 )
and partition_id <=TO_NUMBER( :PARTITION_ID2)
AND ( operate_time>= TO_DATE(:BEGIN_TIME, 'YYYY-MM-DD') or :BEGIN_TIME is null)
AND (operate_time <= TO_DATE(:END_TIME, 'YYYY-MM-DD')+1   or :END_TIME is null)
) b
where  b.partition_id1 =c.partition_id 
AND b.operate_id=c.charge_id
and (rownum<=:ROWNUM or :ROWNUM is null)