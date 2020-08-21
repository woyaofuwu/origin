SELECT c.depart_id,c.depart_code,c.depart_name,c.depart_kind_code,c.area_code,c.rsvalue1,c.rsvalue2,c.update_time,c.update_staff_id,c.update_depart_id,sum(b.money) subj_balance
  FROM tf_chl_acct a, tf_chl_datbook b, td_m_depart c, tf_f_user d 
 WHERE a.rsrv_str1 = :SERIAL_NUMBER
   AND a.rsrv_str1 = d.serial_number
   and a.state in ('R0A','R0Y')
   AND MOD(a.obj_code,10000) = d.partition_id
   AND d.remove_tag = '0'
   AND a.chnl_id = c.depart_id
   AND c.validflag='0'
   AND a.chnl_acct_id = b.chnl_acct_id
   AND b.chnl_deposit in (500,600)
group by 
c.depart_id,c.depart_code,c.depart_name,c.depart_kind_code,c.area_code,c.rsvalue1,c.rsvalue2,c.update_time,c.update_staff_id,c.update_depart_id