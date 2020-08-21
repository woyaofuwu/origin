SELECT a.trade_id para_code1,'' para_code2,'' para_code3,
       '' para_code4,
       '' para_code5,
       a.CUST_NAME para_code6,
       decode(:CANCEL_TAG,'0',to_char(a.ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss'),to_char(a.CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss')) para_code7,
       to_char(a.OPER_FEE) para_code8,to_char(a.FOREGIFT) para_code9,
       to_char(a.ADVANCE_PAY) para_code10,
       a.trade_type_code para_code11,'' para_code12,a.cust_id para_code13,a.CANCEL_TAG para_code14,'0' para_code15,
       a.SERIAL_NUMBER para_code16,f_sys_getcodename('trade_type_code',trade_type_code,:TRADE_EPARCHY_CODE
,NULL) para_code17,f_sys_getcodename('depart_id',TRADE_DEPART_ID,NULL
,NULL) para_code18,a.TRADE_STAFF_ID para_code19,f_sys_getcodename('staff_id',TRADE_STAFF_ID,NULL,NULL) para_code20,
       a.TRADE_DEPART_ID para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM TF_Bh_TRADE a
where trade_id=:TRADE_ID
   and (cancel_tag='0' or cancel_tag='1')