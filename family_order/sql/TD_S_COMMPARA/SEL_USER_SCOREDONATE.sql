SELECT /*+ INDEX(a IDX_TF_B_TRADE_SCORE_SN) INDEX(b PK_TF_BH_TRADE) INDEX(c PK_TF_B_TRADE_SCORE)*/
TO_CHAR(a.trade_id) para_code1,
c.serial_number para_code2,
TO_CHAR(a.user_id) para_code3,
TO_CHAR(NVL(c.score,0)) para_code4,
TO_CHAR(NVL(c.score_changed,0)) para_code5,
a.remark para_code6,
TO_CHAR(b.accept_date,'YYYY-MM-DD HH24:MI:SS') para_code7,
trade_staff_id para_code8,
trade_depart_id para_code9,
DECODE(b.cancel_tag,'0','正常','1','被返销','2','返销') para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_bh_trade b,tf_b_trade_score a,tf_b_trade_score c
WHERE a.trade_id=b.trade_id AND a.accept_month=b.accept_month
AND b.accept_date BETWEEN TO_DATE(:PARA_CODE2,'YYYYMMDD') AND TO_DATE(:PARA_CODE3,'YYYYMMDD')+1
AND a.serial_number=:PARA_CODE1
AND b.trade_type_code=340
and b.trade_id=c.trade_id
AND EPARCHY_CODE=:PARA_CODE4
UNION ALL
SELECT /*+ INDEX(a IDX_TF_B_TRADE_SCORE_SN) INDEX(b PK_TF_B_TRADE) INDEX(c PK_TF_B_TRADE_SCORE)*/
TO_CHAR(a.trade_id),
c.serial_number,
TO_CHAR(a.user_id),
TO_CHAR(NVL(c.score,0)),
TO_CHAR(NVL(c.score_changed,0)),
a.remark,
TO_CHAR(b.accept_date,'YYYY-MM-DD HH24:MI:SS'),
trade_staff_id,
trade_depart_id,
DECODE(b.cancel_tag,'0','正常','1','被返销','2','返销'),
'','','','','',
'','','','','',
'','','' ,'' ,'' ,
'','','','' ,'' ,
'','','' ,'' ,'','','','',0,'',''
FROM tf_b_trade b,tf_b_trade_score a,tf_b_trade_score c
WHERE a.trade_id=b.trade_id AND a.accept_month=b.accept_month
AND b.accept_date BETWEEN TO_DATE(:PARA_CODE2,'YYYYMMDD') AND TO_DATE(:PARA_CODE3,'YYYYMMDD')+1
AND a.serial_number=:PARA_CODE1
AND b.trade_type_code=340
and b.trade_id=c.trade_id
AND EPARCHY_CODE=:PARA_CODE4
AND :PARA_CODE5 IS NULL
AND :PARA_CODE6 IS NULL
AND :PARA_CODE7 IS NULL
AND :PARA_CODE8 IS NULL
AND :PARA_CODE9 IS NULL
AND :PARA_CODE10 IS NULL