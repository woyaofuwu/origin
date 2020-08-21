SELECT a.serial_number para_code1,c.pay_mode para_code2,b.pay_name para_code3,d.bank para_code4,b.bank_acct_no para_code5,
           max(a.accept_date) para_code6,'' para_code7,'' para_code8,'' para_code9,
'' para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
     FROM tf_bh_trade a,tf_f_account b,td_s_paymode c,td_b_bank d
     WHERE a.accept_date >=to_date(:PARA_CODE1,'yyyymmdd')
	     AND a.trade_city_code=:PARA_CODE2
	     AND a.trade_type_code='80'
	     AND a.cust_id=b.cust_id
	     AND (b.pay_mode_code=:PARA_CODE3 OR :PARA_CODE3 = '-1')
	     AND b.pay_mode_code=c.pay_mode_code
	     AND b.bank_code=d.bank_code(+)
              AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
              AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
              AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
              AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
              AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
              AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
              AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)
     GROUP BY b.cust_id,a.serial_number,b.pay_name,
     b.cust_id,c.pay_mode,d.bank,b.bank_acct_no