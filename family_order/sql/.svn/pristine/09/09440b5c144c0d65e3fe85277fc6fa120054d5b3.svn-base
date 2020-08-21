SELECT TO_CHAR(a.trade_id) PARA_CODE1,
TO_CHAR(a.trade_type_code) PARA_CODE2,
a.brand_code PARA_CODE3,
a.serial_number PARA_CODE4,
a.cust_name PARA_CODE5,
TO_CHAR(a.oper_fee/100) PARA_CODE6,
TO_CHAR(a.foregift/100) PARA_CODE7,
TO_CHAR(a.advance_pay/100) PARA_CODE8,
TO_CHAR(b.open_date,'YYYY-MM-DD HH24:MI:SS') PARA_CODE9,
b.user_type_code PARA_CODE10,
b.pspt_type_code PARA_CODE11,
b.pspt_id PARA_CODE12,
b.pspt_addr PARA_CODE13,
b.home_address PARA_CODE14,
b.sex PARA_CODE15,
b.pay_name PARA_CODE16,
b.pay_mode_code PARA_CODE17,
b.bank_acct_no PARA_CODE18,
b.bank_code PARA_CODE19,
b.contract_no PARA_CODE20,
b.contact PARA_CODE21,
b.contact_phone PARA_CODE22,
TO_CHAR(a.accept_date,'YYYY-MM-DD HH24:MI:SS') PARA_CODE23,
a.trade_staff_id PARA_CODE24,
a.trade_depart_id PARA_CODE25,
a.trade_city_code PARA_CODE26,
a.trade_eparchy_code PARA_CODE27,
'' PARA_CODE28,
'' PARA_CODE29,
'' PARA_CODE30,
'' start_date,
'' end_date,
'' eparchy_code,
'' remark,
'' update_staff_id,
'' update_depart_id,
'' update_time,
'' subsys_code,
0 param_attr,
'' param_code,
'' param_name
FROM tf_bh_trade a,tf_bh_trade_detail b,tf_b_trade_audit c
WHERE a.trade_id=b.trade_id(+)
AND a.cancel_tag='0'
AND a.in_mode_code='0'
AND a.serial_number=:PARA_CODE1
AND a.accept_date BETWEEN TO_DATE(:PARA_CODE2,'YYYYMMDD')  AND  TO_DATE(:PARA_CODE3,'YYYYMMDD')+1
AND a.trade_city_code=:PARA_CODE4
AND a.trade_eparchy_code=:PARA_CODE5
AND EXISTS (select 1 FROM td_s_commpara WHERE para_code1=TO_CHAR(a.trade_type_code)
AND param_attr=983
AND param_code='AUDIT'
AND subsys_code='CSM'
AND eparchy_code=:PARA_CODE5
AND para_code1 LIKE :PARA_CODE6)
AND a.trade_id=c.trade_id(+)
AND c.trade_id IS NULL
AND :PARA_CODE7 IS NULL
AND :PARA_CODE8 IS NULL
AND :PARA_CODE9 IS NULL
AND :PARA_CODE10 IS NULL