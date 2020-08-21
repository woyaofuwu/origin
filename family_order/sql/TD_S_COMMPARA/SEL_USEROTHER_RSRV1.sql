SELECT decode((SELECT COUNT(1) FROM tf_bh_trade a, td_s_tradetype b 
where accept_date > (select max(accept_date) from tf_bh_trade where user_id = to_number(:PARA_CODE1)
and trade_type_code = 630 and rsrv_str1 = 12000000 ) and user_id = to_number(:PARA_CODE1)
and a.trade_type_code in (192, 100, 7230, 7240, 7302,632)
and a.trade_type_code = b.trade_type_code and a.eparchy_code = b.eparchy_code),0,'出保','不出保') para_code1,
rsrv_str9 para_code2,rsrv_value para_code3,rsrv_str10 para_code4,rsrv_str1 para_code5,
rsrv_str3 para_code6,rsrv_str6 para_code7,rsrv_str8 para_code8,start_date para_code9,
end_date para_code10,
'' para_code11,rsrv_str2 para_code12,rsrv_str4 para_code13,rsrv_str5 para_code14,rsrv_str7 para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
start_date start_date, end_date end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_f_user_other
WHERE  partition_id = MOD(to_number(:PARA_CODE1),10000)
   AND user_id = to_number(:PARA_CODE1)
   AND rsrv_value_code = :PARA_CODE2
   AND rsrv_value = :PARA_CODE3
   AND end_date > SYSDATE
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)