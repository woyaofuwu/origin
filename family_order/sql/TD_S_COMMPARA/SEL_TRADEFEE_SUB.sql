SELECT trade_id para_code1,decode(FEE_MODE,'0','营业费','1','押金','2','预存','其他') para_code2,
       decode(FEE_MODE,'0',f_sys_getcodename('feeitem_code',FEE_TYPE_CODE,NULL,NULL),'1',f_sys_getcodename('foregift_code',FEE_TYPE_CODE,NULL,NULL),'预存费用')  para_code3,
       OLDFEE para_code4,
       FEE para_code5,
       '' para_code6,
       '' para_code7,
       '' para_code8,'' para_code9,
       '' para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM TF_B_TRADEFEE_SUB
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))