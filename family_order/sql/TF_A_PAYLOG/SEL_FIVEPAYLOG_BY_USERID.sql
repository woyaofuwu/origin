SELECT TO_CHAR(a.CHARGE_ID) CHARGE_ID,
       a.PARTITION_ID,a.EPARCHY_CODE,
       a.CITY_CODE,TO_CHAR(a.CUST_ID) CUST_ID,
       TO_CHAR(a.USER_ID) USER_ID,TO_CHAR(a.ACCT_ID) ACCT_ID,
       a.CHARGE_SOURCE_CODE,a.PAY_FEE_MODE_CODE,
       TO_CHAR(NVL(a.RECV_FEE, '0')) RECV_FEE,
       a.DEPOSIT_CODE,a.CANCEL_TAG,
       TO_CHAR(a.RECV_TIME, 'yyyy-mm-dd hh24:mi:ss') RECV_TIME,
       a.RECV_EPARCHY_CODE,a.RECV_CITY_CODE,a.RECV_DEPART_ID,a.RECV_STAFF_ID,
       TO_CHAR(a.CANCEL_TIME, 'yyyy-mm-dd hh24:mi:ss') CANCEL_TIME,
       a.CANCEL_EPARCHY_CODE,a.CANCEL_CITY_CODE,a.CANCEL_DEPART_ID,a.CANCEL_STAFF_ID
FROM 
(SELECT charge_id,partition_id,eparchy_code,city_code,cust_id,user_id,acct_id,charge_source_code,
pay_fee_mode_code,recv_fee,deposit_code,cancel_tag,recv_time,recv_eparchy_code,recv_city_code,
recv_depart_id,recv_staff_id,cancel_time,cancel_eparchy_code,cancel_city_code,
cancel_depart_id,cancel_staff_id,ROW_NUMBER() OVER(ORDER BY RECV_TIME DESC) ROW_NUMBER
FROM 
(SELECT charge_id,partition_id,eparchy_code,city_code,cust_id,user_id,acct_id,charge_source_code,
pay_fee_mode_code,recv_fee,deposit_code,cancel_tag,recv_time,recv_eparchy_code,recv_city_code,
recv_depart_id,recv_staff_id,cancel_time,cancel_eparchy_code,cancel_city_code,
cancel_depart_id,cancel_staff_id
FROM Tf_a_Paylog_rc a
WHERE a.user_id=to_number(:USER_ID) AND a.eparchy_code=:EPARCHY_CODE AND a.CANCEL_TAG='0' UNION 
SELECT charge_id,partition_id,eparchy_code,city_code,cust_id,user_id,acct_id,charge_source_code,
pay_fee_mode_code,recv_fee,deposit_code,cancel_tag,recv_time,recv_eparchy_code,recv_city_code,
recv_depart_id,recv_staff_id,cancel_time,cancel_eparchy_code,cancel_city_code,
cancel_depart_id,cancel_staff_id
FROM Tf_a_Paylog a
WHERE a.user_id=to_number(:USER_ID) AND a.eparchy_code=:EPARCHY_CODE AND a.CANCEL_TAG='0' ) ) a
WHERE ROW_NUMBER<=5