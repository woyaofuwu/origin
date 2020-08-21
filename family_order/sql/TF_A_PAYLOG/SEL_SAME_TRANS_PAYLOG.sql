SELECT to_char(charge_id) charge_id,
       partition_id,
       eparchy_code,
       city_code,
       to_char(cust_id) cust_id,
       to_char(user_id) user_id,
       to_char(acct_id) acct_id,
       charge_source_code,
       pay_fee_mode_code,
       to_char(recv_fee) recv_fee,
       deposit_code,
       cancel_tag,
       to_char(recv_time, 'yyyy-mm-dd hh24:mi:ss') recv_time,
       recv_eparchy_code,
       recv_city_code,
       recv_depart_id,
       recv_staff_id,
       to_char(cancel_time, 'yyyy-mm-dd hh24:mi:ss') cancel_time,
       cancel_eparchy_code,
       cancel_city_code,
       cancel_depart_id,
       cancel_staff_id
  FROM tf_a_paylog a
 WHERE acct_id = to_number(:ACCT_ID)
   AND partition_id >= to_number(:START_PARTITION_ID)
   AND partition_id <= to_number(:END_PARTITION_ID)
   AND recv_time >= to_date(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND recv_time <= to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND cancel_tag = '0'
   AND charge_source_code IN
       (select tag_number
          FROM TD_S_TAG b
         WHERE b.eparchy_code = a.eparchy_code
           and tag_code like 'ASM_SAMETRANS_CHARGESOURCE%')