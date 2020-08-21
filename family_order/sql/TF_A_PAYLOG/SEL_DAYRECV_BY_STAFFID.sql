SELECT 
       NULL charge_id,
       0 partition_id,
       NULL eparchy_code,
       NULL city_code,
       NULL cust_id,
       NULL user_id,
       NULL acct_id,
       0 charge_source_code,
       pay_fee_mode_code,
       SUM(RECV_FEE) recv_fee,
       0 deposit_code,
       NULL cancel_tag,
       NULL recv_time,
       NULL recv_eparchy_code,
       NULL recv_city_code,
       NULL recv_depart_id,
       NULL recv_staff_id,
       SUM(DECODE(CANCEL_TAG,'1',RECV_FEE,0)) cancel_time,
       NULL cancel_eparchy_code,
       NULL cancel_city_code,
       NULL cancel_depart_id,
       NULL cancel_staff_id
 FROM TF_A_PAYLOG
 WHERE RECV_STAFF_ID=:RECV_STAFF_ID  AND RECV_TIME >= TRUNC(SYSDATE)
  AND PARTITION_ID= TO_NUMBER(TO_CHAR(SYSDATE,'MM')) AND CANCEL_TAG<>'2'
  and charge_source_code in
       (SELECT charge_source_code
          FROM td_sd_chargesource a
         WHERE a.act_type = '1'
           AND a.valid_tag = '1')
 GROUP BY PAY_FEE_MODE_CODE