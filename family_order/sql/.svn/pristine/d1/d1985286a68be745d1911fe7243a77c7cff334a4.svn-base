--IS_CACHE=Y
select to_char(a.info_code) para_code1,a.info_value para_code2,a.BUSI_INTRO para_code3,to_char(a.START_DATE,'yyyy-mm-dd hh24:mi:ss') para_code4,to_char(a.END_DATE,'yyyy-mm-dd hh24:mi:ss') para_code5,a.RSRV_STR1 para_code6,a.RSRV_STR2 para_code7,a.RSRV_STR3 para_code8,a.RSRV_STR4 para_code9,a.RSRV_STR5 para_code10,a.update_staff_id para_code11 ,a.update_depart_id para_code12,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') para_code13,'' para_code14,'' para_code15,'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,'' subsys_code,0 param_attr,'' param_code,'' param_name,'' remark,'' start_date,'' end_date, '' eparchy_code,'' update_staff_id,'' update_depart_id,'' update_time
 from ucr_crm1.TD_B_PRODUCT_INTRODUCE a
where ((:PARA_CODE1 = '' OR :PARA_CODE1 IS NULL) OR(a.info_code = to_number(:PARA_CODE1)))
   AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)
   AND sysdate between a.start_date and a.end_date