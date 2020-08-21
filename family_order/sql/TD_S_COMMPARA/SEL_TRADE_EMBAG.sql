SELECT a.IMEI para_code1,a.PURCHASE_DESC para_code2,
       a.RPAY_DEPOSIT para_code3,
       b.RSRV_STR8 para_code4,
       b.RSRV_STR9 para_code5,
       '' para_code6,
       b.RSRV_STR10 para_code7,
       '' para_code8,'' para_code9,
       '' para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM TF_B_TRADE_PURCHASE a,tf_bh_trade b
 WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.trade_id=b.trade_id