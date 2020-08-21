SELECT '营业费用' PARA_CODE1,TO_CHAR(a.oldfee/100) PARA_CODE2,TO_CHAR(a.fee/100) PARA_CODE3,b.feeitem_name PARA_CODE4,'' para_code5,
'' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_b_tradefee_sub a,td_b_feeitem b
WHERE a.fee_type_code=b.feeitem_code
AND trade_id =:PARA_CODE1   AND eparchy_code=:PARA_CODE2
AND fee_mode='0'
UNION ALL
SELECT '押金金额',TO_CHAR(a.oldfee/100),TO_CHAR(a.fee/100),b.foregift_name,'' ,
'' , '', '', '', '',
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'','' ,'','','','',0,'',''
FROM tf_b_tradefee_sub a,td_s_foregift b
WHERE a.fee_type_code=b.foregift_code
AND trade_id =:PARA_CODE1
AND fee_mode='1'
UNION all
SELECT '押金金额',TO_CHAR(a.oldfee/100),TO_CHAR(a.fee/100),b.deposit_name,'' ,
'' , '', '', '', '',
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'','' ,'','','','',0,'',''
FROM tf_b_tradefee_sub a,td_a_depositpriorrule b
WHERE a.fee_type_code=b.deposit_code
AND trade_id =:PARA_CODE1 AND eparchy_code=:PARA_CODE2
AND fee_mode='2'
UNION ALL
SELECT '类型['||FEE_MODE||']',TO_CHAR(oldfee/100),TO_CHAR(fee/100),TO_CHAR(fee_type_code),'' ,
'' , '', '', '', '',
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'' ,'' ,'' ,
'' ,'' ,'','' ,'','','','',0,'',''
FROM tf_b_tradefee_sub
WHERE  trade_id =:PARA_CODE1
AND fee_mode NOT IN('0','1','2')
AND :PARA_CODE3 IS NULL
AND :PARA_CODE4 IS NULL
AND :PARA_CODE5 IS NULL
AND :PARA_CODE6 IS NULL
AND :PARA_CODE7 IS NULL
AND :PARA_CODE8 IS NULL
AND :PARA_CODE9 IS NULL
AND :PARA_CODE10 IS NULL