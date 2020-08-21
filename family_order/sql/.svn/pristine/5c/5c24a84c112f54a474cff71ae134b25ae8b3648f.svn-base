SELECT a.serial_number para_code1,b.pay_name para_code2,
b.acct_id para_code3,(select pay_mode from td_s_paymode where pay_mode_code = b.pay_mode_code ) para_code4,
b.pay_mode_code para_code5,'' para_code6,'' para_code7,'' para_code8, '' PARA_CODE9 , 
b.rsrv_str3 PARA_CODE10 , b.rsrv_str10 PARA_CODE11 , b.contract_no PARA_CODE12 , '' PARA_CODE13 , '' PARA_CODE14 , '' PARA_CODE15 , '' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 , '' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 , '' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 , '' PARA_CODE28 ,'' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , '' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , '' UPDATE_DEPART_ID , '' UPDATE_TIME,'' subsys_code,0 param_attr,'' param_code,'' param_name
 from tf_f_user a ,tf_f_account b
where   a.remove_tag='0'
   AND b.bank_code=:PARA_CODE1 
   AND b.bank_acct_no=:PARA_CODE2
   AND b.remove_tag='0'
   AND a.cust_id=b.cust_id
    AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL) and
        (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL) and
        (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL) and
        (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL) and
        (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL) and
        (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL) and
        (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL) and
        (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)