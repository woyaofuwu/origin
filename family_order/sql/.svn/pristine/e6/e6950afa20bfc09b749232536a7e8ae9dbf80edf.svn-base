SELECT a.trade_id para_code1,'' para_code2,'' para_code3,
       '' para_code4,
       '' para_code5,
       a.CUST_NAME para_code6,
       to_char(a.ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') para_code7,
       to_char(a.OPER_FEE) para_code8,to_char(a.FOREGIFT) para_code9,
       to_char(a.ADVANCE_PAY) para_code10,
       a.trade_type_code para_code11,'' para_code12,a.cust_id para_code13,a.CANCEL_TAG para_code14,'0' para_code15,
       a.SERIAL_NUMBER para_code16,b.STATE_CODE para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM TF_B_TRADE a,TS_A_AUDITRESULT_DETAIL b
where a.accept_date>trunc(TO_DATE(:AUDIT_DATE,'YYYY-MM-DD HH24:MI:SS'))
AND a.accept_date<trunc(TO_DATE(:AUDIT_DATE,'YYYY-MM-DD HH24:MI:SS'))+1
AND a.TRADE_DEPART_ID = :TRADE_DEPART_ID 
AND a.TRADE_STAFF_ID = :TRADE_STAFF_ID
AND a.TRADE_TYPE_CODE= :TRADE_TYPE_CODE
AND (a.cancel_tag='0' OR a.cancel_tag='2' or ((a.cancel_tag='1')AND(a.EXEC_TIME>a.FINISH_DATE)))
AND a.trade_id=b.trade_id