select t1.*  from tf_f_customer t,tf_f_user t1
where t.cust_id=t1.cust_id
and t.pspt_id=:PSPT_ID
and t1.remove_tag='0'
and t1.serial_number like 'KD_%'