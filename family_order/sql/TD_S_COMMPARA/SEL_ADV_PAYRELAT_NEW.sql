SELECT :PARA_CODE1 para_code1,d.serial_number para_code2,a.payitem_code para_code3,
       decode(a.payitem_code,'-1','全部付费','部分付费('||f_sys_getcodename('payitem_code',a.payitem_code,:PARA_CODE6,NULL)||')') para_code4,
	     f_sys_getcodename('acyc_id',a.start_acyc_id,NULL,NULL) para_code5,
       f_sys_getcodename('acyc_id',a.end_acyc_id,NULL,NULL) para_code6,
       decode(a.limit_type,'0','不限定','1','按金额','2','按比例','其他') para_code7,
       to_number(nvl(a.LIMIT,0))/100.0 para_code8, decode(a.complement_tag,'0','不补足','1','补足','其他') para_code9,
       '' para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
	FROM tf_a_payrelation a,tf_f_user d
 WHERE    a.user_id!=:PARA_CODE2 
	 AND a.acct_id=:PARA_CODE3
	 AND (:PARA_CODE4='0' OR :PARA_CODE5 BETWEEN a.start_acyc_id AND a.end_acyc_id)
	 AND a.start_acyc_id<=a.end_acyc_id
	 AND d.user_id=a.user_id
         
         AND EXISTS(
            select 1
            from tf_f_user b,TF_F_CUSTOMER c
            where b.cust_id=c.cust_id
            AND c.cust_type='1'
            AND b.user_id=:PARA_CODE2)
         AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
         AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
         AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
         AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)
         AND a.act_tag='1'
ORDER BY 1,2,3