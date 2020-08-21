INSERT INTO tf_a_paylog_center(charge_id,partition_id,eparchy_code,city_code,cust_id,user_id,acct_id,charge_source_code,pay_fee_mode_code,recv_fee,deposit_code,cancel_tag,recv_time,recv_eparchy_code,recv_city_code,recv_depart_id,recv_staff_id,cancel_time,cancel_eparchy_code,cancel_city_code,cancel_depart_id,cancel_staff_id,serial_number)
 VALUES(TO_NUMBER(:CHARGE_ID),:PARTITION_ID,:EPARCHY_CODE,:CITY_CODE,TO_NUMBER(:CUST_ID),TO_NUMBER(:USER_ID),TO_NUMBER(:ACCT_ID),:CHARGE_SOURCE_CODE,:PAY_FEE_MODE_CODE,TO_NUMBER(:RECV_FEE),:DEPOSIT_CODE,:CANCEL_TAG,TO_DATE(:RECV_TIME,'YYYY-MM-DD HH24:MI:SS'),:RECV_EPARCHY_CODE,:RECV_CITY_CODE,:RECV_DEPART_ID,:RECV_STAFF_ID,TO_DATE(:CANCEL_TIME,'YYYY-MM-DD HH24:MI:SS'),:CANCEL_EPARCHY_CODE,:CANCEL_CITY_CODE,:CANCEL_DEPART_ID,:CANCEL_STAFF_ID,:SERIAL_NUMBER)