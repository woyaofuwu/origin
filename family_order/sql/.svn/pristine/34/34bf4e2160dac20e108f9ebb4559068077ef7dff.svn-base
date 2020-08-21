SELECT b.rsrv_str3 para_code1,
TO_CHAR(c.bcyc_id) para_code2,
TO_CHAR(nvl(d.bcyc_id,'205012')) para_code3,
TO_CHAR(NVL(b.rsrv_str6/100,0)) para_code4,
TO_CHAR(NVL(b.rsrv_str7/100,0)) para_code5,
a.rsrv_str2 para_code6, e.para_code1 para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_bh_trade a,tf_b_trade_other b,td_a_acycpara c,td_a_acycpara d,td_s_commpara e
WHERE a.trade_id=b.trade_id
AND cancel_tag<'2'
AND a.trade_id=TO_NUMBER(:PARA_CODE1)
AND a.accept_month = TO_NUMBER(SUBSTR(:PARA_CODE1,5,2))
AND b.rsrv_value_code='SPDE'
AND e.param_attr=79 AND e.eparchy_code='ZZZZ'
AND a.rsrv_str2=e.param_code(+)
AND b.rsrv_str4=c.acyc_id(+)
AND b.rsrv_str5=d.acyc_id(+)
UNION ALL
SELECT b.rsrv_str3 ,
TO_CHAR(c.bcyc_id) ,
TO_CHAR(nvl(d.bcyc_id,'205012')) ,
TO_CHAR(NVL(b.rsrv_str6/100,0)) ,
TO_CHAR(NVL(b.rsrv_str7/100,0)) ,
a.rsrv_str2 para_code6, e.para_code1 para_code7, '', '', '',
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,'' ,'' ,'' ,0 ,'' ,''
FROM tf_b_trade a,tf_b_trade_other b,td_a_acycpara c,td_a_acycpara d,td_s_commpara e
WHERE a.trade_id=b.trade_id
AND cancel_tag<'2'
AND a.trade_id=TO_NUMBER(:PARA_CODE1)
AND a.accept_month = TO_NUMBER(SUBSTR(:PARA_CODE1,5,2))
AND b.rsrv_value_code='SPDE'
AND e.param_attr=79 AND e.eparchy_code='ZZZZ'
AND a.rsrv_str2=e.param_code(+)
AND b.rsrv_str4=c.acyc_id(+)
AND b.rsrv_str5=d.acyc_id(+)
AND (:PARA_CODE2 IS NULL)
AND (:PARA_CODE3 IS NULL)
AND (:PARA_CODE4 IS NULL)
AND (:PARA_CODE5 IS NULL)
AND (:PARA_CODE6 IS NULL)
AND (:PARA_CODE7 IS NULL)
AND (:PARA_CODE8 IS NULL)
AND (:PARA_CODE9 IS NULL)
AND (:PARA_CODE10 IS NULL)