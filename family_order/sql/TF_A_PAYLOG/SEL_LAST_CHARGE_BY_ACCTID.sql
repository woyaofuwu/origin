SELECT to_char(charge_id) charge_id,partition_id,eparchy_code,
	city_code,to_char(cust_id) cust_id,to_char(user_id) user_id,
	to_char(acct_id) acct_id,charge_source_code,pay_fee_mode_code,
	to_char(recv_fee) recv_fee,deposit_code,cancel_tag,
	to_char(recv_time,'yyyy-mm-dd hh24:mi:ss') recv_time,
	recv_eparchy_code,recv_city_code,recv_depart_id,recv_staff_id 
  FROM tf_a_paylog
 WHERE  acct_id=TO_NUMBER(:ACCT_ID)
   AND RECV_TIME < TO_DATE(:RECV_TIME,'YYYY-MM-DD HH24:MI:SS')
   AND CHARGE_SOURCE_CODE NOT IN (SELECT TAG_INFO FROM TD_S_TAG WHERE TAG_CODE like 'ASM_CASH_PAYLOG%' AND USE_TAG='1')
   AND cancel_tag='0' ORDER BY RECV_TIME DESC