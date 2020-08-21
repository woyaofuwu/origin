select 
distinct b.param_name para_code1,
decode(a.param_value,'0',decode(a.param_code,'CRDFLG','有效','0'),
                     '1',decode(a.param_code,'CRDFLG','已使','1'),
                     '2',decode(a.param_code,'CRDFLG','清退','2'),
                     '3',decode(a.param_code,'CRDFLG','生成','3'),
                     '4',decode(a.param_code,'CRDFLG','封闭状态','4'),
                     '5',decode(a.param_code,'CRDFLG','过期','5'),
                     decode(a.param_code,'CTOTAL',to_char(to_number(a.param_value)/100),a.param_value)) para_code2,
'' para_code3,
'' para_code4, '' para_code5,
'' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  from ti_c_olcomcommonquery a,td_s_commpara b,ti_c_olcomwork c
where b.param_attr=120
and b.subsys_code='CSM'
and b.para_code1 = a.param_code
and a.olcom_work_id = c.olcom_work_id
and c.trade_id=to_number(:PARA_CODE1)
and c.olcom_state='3'
and not exists(select 1 from ti_c_olcomwork d where d.trade_id = c.trade_id and d.olcom_state<>'3')
   AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)