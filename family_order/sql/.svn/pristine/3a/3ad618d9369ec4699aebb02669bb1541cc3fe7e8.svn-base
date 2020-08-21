SELECT charge_id,partition_id ,eparchy_code,city_code, cust_id, user_id, acct_id,charge_source_code,pay_fee_mode_code, recv_fee,deposit_code,cancel_tag,recv_time,recv_eparchy_code,recv_city_code,recv_depart_id,recv_staff_id,cancel_time,
cancel_eparchy_code,cancel_city_code,cancel_depart_id,cancel_staff_id from (
SELECT to_char(charge_id) charge_id,a.partition_id partition_id ,a.eparchy_code eparchy_code,city_code,to_char(cust_id) cust_id,to_char(user_id) user_id,to_char(a.acct_id) acct_id,charge_source_code,pay_fee_mode_code,to_char(recv_fee) recv_fee,deposit_code,a.cancel_tag ,to_char(recv_time,'yyyy-mm-dd hh24:mi:ss') recv_time,recv_eparchy_code,recv_city_code,recv_depart_id,recv_staff_id,to_char(cancel_time,'yyyy-mm-dd hh24:mi:ss') cancel_time,cancel_eparchy_code,cancel_city_code,cancel_depart_id,cancel_staff_id 
  FROM tf_a_paylog a,tf_a_writesnap_log b
 WHERE a.user_id=TO_NUMBER(:USER_ID)
   AND a.cancel_tag='0'
and a.recv_fee =:RECV_FEE 
and a.recv_time>=sysdate-to_number(:INTERVAL)
and a.partition_id>=to_number(to_char(sysdate,'mm'))-1 and a.partition_id <=to_number(to_char(sysdate,'mm'))
and a.partition_id =b.partition_id and a.charge_id=b.operate_id and b.rsrv_info1=:RSRV_INFO1
union all 
select  to_char(charge_id) charge_id,partition_id ,eparchy_code,city_code,to_char(cust_id) cust_id,to_char(user_id) user_id,to_char(acct_id) acct_id,charge_source_code,pay_fee_mode_code,to_char(recv_fee) recv_fee,deposit_code,cancel_tag ,to_char(recv_time,'yyyy-mm-dd hh24:mi:ss')   recv_time,recv_eparchy_code,recv_city_code,recv_depart_id,recv_staff_id,to_char(cancel_time,'yyyy-mm-dd hh24:mi:ss') cancel_time,cancel_eparchy_code,cancel_city_code,cancel_depart_id,cancel_staff_id 
from tf_a_paylog_emer where 
user_id=TO_NUMBER(:USER_ID) 
and recv_fee =:RECV_FEE
and CHARGE_SOURCE_CODE = 46 and result_info = :RSRV_INFO1 and deal_tag = '0' and cancel_tag = '0'
and recv_time>=sysdate-to_number(:INTERVAL) 
and exists(select 1 from td_s_tag where tag_code = 'ASM_DRECV_PROCESSTAG' and tag_char <>'0')) order by charge_id desc