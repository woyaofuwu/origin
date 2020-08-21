select to_char(count(c.oper_code)) para_code1,c.OPER_CODE para_code2,c.OPER_NAME para_code3,decode(c.STATE,'1','正常','0','作废','其他状态') para_code4,a.cumu_acyc para_code5,a.cumu_id para_code6,to_char(c.score)  para_code7,'' para_code8,'' para_code9,'' para_code10,'' para_code11 ,'' para_code12,'' para_code13,'' para_code14,'' para_code15,'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,'' subsys_code,0 param_attr,'' param_code,'' param_name,'' remark,'' start_date,'' end_date, '' eparchy_code,'' update_staff_id,'' update_depart_id,'' update_time
 from ucr_crm1.chnl_cu_regi a,ucr_crm1.chnl_cu_para c
where a.cumu_id = :PARA_CODE1
   and a.CUMU_ACYC = :PARA_CODE2
   and a.OPER_CODE = c.OPER_CODE 
   and c.STATE = '1' and c.PARA_TYPE = 1
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)
group by c.OPER_CODE,c.OPER_NAME,c.STATE,a.cumu_acyc,a.cumu_id,c.score