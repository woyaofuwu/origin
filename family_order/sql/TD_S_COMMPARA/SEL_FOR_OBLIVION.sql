SELECT a.trade_staff_id para_code1,a.trade_type_code
 para_code2,
       count(*) para_code3,
       f_sys_getcodename('trade_type_code',trade_type_code,:TRADE_EPARCHY_CODE,NULL) para_code4,
       f_sys_getcodename('staff_id',TRADE_STAFF_ID,NULL,NULL) para_code5,
       '' para_code6,
       '' para_code7,
       '' para_code8,'' para_code9,
       '' para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM TS_A_AUDITRESULT_DETAIL a
where a.accept_date>trunc(TO_DATE(:AUDIT_DATE,'YYYY-MM-DD HH24:MI:SS'))  
  AND a.accept_date<trunc(TO_DATE(:AUDIT_DATE,'YYYY-MM-DD HH24:MI:SS'))+1
  AND TRADE_DEPART_ID = :TRADE_DEPART_ID
  group by trade_staff_id,trade_type_code