SELECT b.serial_number para_code1,a.user_id para_code2,a.acct_id para_code3,a.payitem_code para_code4, f_sys_getcodename('payitem_code',a.payitem_code,b.eparchy_code,NULL) para_code5,
f_sys_getcodename('acyc_id',a.start_acyc_id,NUll,NULL) para_code6,f_sys_getcodename('acyc_id',a.end_acyc_id,NUll,NULL) para_code7,a.update_staff_id para_code8,
a.update_time para_code9,decode(a.limit_type,'0','不限定','1','金额','2','比例') para_code10,a.limit/100 para_code11,e.serial_number para_code12,e.cust_name para_code13,
'' para_code14,'' para_code15,'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,'' para_code21,'' para_code22,
'' para_code23,'' para_code24,'' para_code25,'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_a_payrelation a,tf_f_user b,(select c.acct_id,f.cust_name,d.serial_number from tf_f_account c,tf_F_user d,TF_F_CUSTOMER f where d.serial_number = :PARA_CODE1 and d.cust_id = c.cust_id and d.remove_tag = '0' and d.cust_id = f.cust_id) e
 WHERE a.user_id = b.user_id
   AND a.partition_id = b.partition_id
   AND b.remove_tag = '0'
   AND a.acct_id = e.acct_id
   and a.payitem_code = 1700
   AND (:PARA_CODE2 IS NOT NULL OR :PARA_CODE2 IS NULL)
   AND (:PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)