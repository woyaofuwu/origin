select d.serial_number para_code1,d.user_id para_code2,d.acct_id para_code3,d.payitem_code para_code4, f_sys_getcodename('payitem_code',d.payitem_code,c.eparchy_code,NULL) para_code5,
f_sys_getcodename('acyc_id',d.start_acyc_id,NUll,NULL) para_code6,f_sys_getcodename('acyc_id',d.end_acyc_id,NUll,NULL) para_code7,d.update_staff_id para_code8,
d.update_time para_code9,decode(d.limit_type,'0','不确定','1','金额','2','比例') para_code10,d.limit/100 para_code11,c.serial_number para_code12,e.cust_name para_code13,
'' para_code14,'' para_code15,'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,'' para_code21,'' para_code22,
'' para_code23,'' para_code24,'' para_code25,'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  from tf_f_user c, TF_F_CUSTOMER e, (select a.*,b.serial_number from TF_F_USER_SPECIALEPAY a,tf_f_user b where a.user_id = b.user_id and a.PAYITEM_CODE = 1700 and b.serial_number = :PARA_CODE2 and b.remove_tag = '0') d
where c.user_id = d.user_id_a
    and c.cust_id = e.cust_id
   AND (:PARA_CODE1 IS NOT NULL OR :PARA_CODE1 IS NULL)
   AND (:PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)