SELECT (select serial_number from tf_f_user where cust_id = (
           select cust_id from tf_f_account where acct_id = :PARA_CODE3) and user_id = d.user_id
           and remove_tag='0' and (brand_code like 'G0%' or brand_code = 'GP01')) para_code1,
       d.serial_number para_code2,
       f_sys_getcodename('acyc_id',a.start_acyc_id,NULL,NULL) para_code3,
       f_sys_getcodename('acyc_id',a.end_acyc_id,NULL,NULL) para_code4,a.Update_Time para_code5,
       a.Update_staff_id para_code6,
      f_sys_getcodename('depart_id',a.Update_Depart_Id,NULL,NULL) para_code7,
 '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
	FROM tf_a_payrelation a,tf_f_user d
 WHERE  a.acct_id=:PARA_CODE3
	 AND a.act_tag='1' AND a.default_tag='1'
	 AND (:PARA_CODE5 = '0' OR :PARA_CODE4 BETWEEN a.start_acyc_id AND a.end_acyc_id)
	 AND a.start_acyc_id<=(a.end_acyc_id+1)
	 AND d.user_id=a.user_id
         AND d.remove_tag='0'
         AND (:PARA_CODE1 IS NOT NULL)
         AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
         AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
         AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
         AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
         AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)
         AND (:PARA_CODE2 IS NULL OR :PARA_CODE2 IS NOT NULL)
ORDER BY 1,2