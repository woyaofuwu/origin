select to_char(acct_id) acct_id,deposit_code,to_char(money) money,access_tag
 from tf_a_accesslog
where operate_id = :OPERATE_ID
  and partition_id >=:PARTITION_ID - 1
  and partition_id <=:PARTITION_ID + 1
  and operate_type='1'
  and cancel_tag ='1'
union all   
select to_char(acct_id) acct_id,deposit_code,to_char(money) money,access_tag
 from tf_a_accesslog 
where operate_id in (select writeoff_id from tf_a_writeofflog 
where charge_id=:OPERATE_ID  
  and partition_id >=:PARTITION_ID - 1
  and partition_id <=:PARTITION_ID + 1 and rownum <=1)
  and operate_type='2'
  and cancel_tag ='1'