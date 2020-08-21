SELECT recv_staff_id staff_id,
       f_sys_getcodename('staff_id',recv_staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',recv_depart_id,NULL,NULL) depart_name,
       TO_CHAR(charge_id) charge_id,
       f_sys_getcodename('pay_sfee_mode_code',a.pay_fee_mode_code,NULL,NULL) pay_fee_mode,
       f_sys_getcodename('charge_source_code',charge_source_code,NULL,NULL) charge_source,
       f_sys_getcodename('serial_number',user_id,NULL,NULL) serial_number,
       TO_CHAR(recv_time,'YYYYMMDD HH24:MI:SS') recv_time,
       recv_fee/100 recv_fee,
       '返销' cancel_tag,
       cancel_staff_id
  FROM tf_a_paylog_rc a
 WHERE a.cancel_tag IN ('2')
   AND a.recv_time >= TO_DATE(:start_date,'YYYYMMDD')
   AND a.recv_time <  TO_DATE(:end_date,'YYYYMMDD') + 1
   AND a.recv_depart_id >= :start_depart_id
   AND a.recv_depart_id <= :end_depart_id
   AND a.partition_id = TO_NUMBER(SUBSTR(:start_date,5,2))
   AND EXISTS (SELECT 1
                 FROM td_sd_chargesource c
                WHERE c.act_type = '1'  
                  AND c.valid_tag = '1'  
                  AND a.charge_source_code = c.charge_source_code)
 UNION ALL
SELECT recv_staff_id staff_id,
       f_sys_getcodename('staff_id',recv_staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',recv_depart_id,NULL,NULL) depart_name,
       TO_CHAR(charge_id) charge_id,
       f_sys_getcodename('pay_fee_mode_code',a.pay_fee_mode_code,NULL,NULL) pay_fee_mode,
       f_sys_getcodename('charge_source_code',charge_source_code,NULL,NULL) charge_source,
       f_sys_getcodename('serial_number',user_id,NULL,NULL) serial_number,
       TO_CHAR(recv_time,'YYYYMMDD HH24:MI:SS') recv_time,
       recv_fee/100 recv_fee,
       '返销' cancel_tag,
       cancel_staff_id
  FROM tf_a_paylog a
 WHERE a.cancel_tag IN ('2')
   AND a.recv_time >= TO_DATE(:start_date,'YYYYMMDD')
   AND a.recv_time <  TO_DATE(:end_date,'YYYYMMDD') + 1
   AND a.recv_depart_id >= :start_depart_id
   AND a.recv_depart_id <= :end_depart_id
   AND a.partition_id = TO_NUMBER(SUBSTR(:start_date,5,2))
   AND EXISTS (SELECT 1
                 FROM td_sd_chargesource c
                WHERE c.act_type = '1'  
                  AND c.valid_tag = '1'  
                  AND a.charge_source_code = c.charge_source_code)