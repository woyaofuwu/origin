SELECT f_csb_getaccountserialnumber(:ACCT_ID) serial_number_a,
       d.serial_number serial_number_b,
       start_cycle_id,end_cycle_id,
       to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
       a.update_staff_id update_staff_id,
       a.update_depart_id update_depart_id
  FROM tf_a_payrelation a,tf_f_user d
 WHERE   a.acct_id=:ACCT_ID
   AND a.act_tag='1' AND a.default_tag='1'
   AND (:PAYRELATION_QUERY_TYPE = '1' OR (to_number(to_char(sysdate, 'yyyymmdd')) 
                                   BETWEEN a.start_cycle_id 
                                           AND a.end_cycle_id))
   AND a.start_cycle_id<=(a.end_cycle_id+1)
   AND d.user_id=a.user_id
         AND d.remove_tag='0'