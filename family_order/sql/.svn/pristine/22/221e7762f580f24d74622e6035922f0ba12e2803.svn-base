select a.supply_dtl_id para_code1, ( select namebrand_desc from td_s_namebrand where namebrand_code = a.namebrand_code ) para_code2, ( select device_model from td_s_device_model where eparchy_code = a.eparchy_code and device_model_code = a.device_model_code AND device_type_code = '0' ) para_code3, ( select color from td_s_device_color where eparchy_code = a.eparchy_code and color_code = a.color_code AND device_type_code = '0' ) para_code4, ( select fitting from td_s_device_fitting where eparchy_code = a.eparchy_code and fitting_code = a.fitting_code AND device_type_code = '0' ) para_code5, to_char( a.supply_num ) para_code6, to_char( a.pre_in_num ) para_code7, to_char( a.acpt_num ) para_code8, '' para_code9, '' para_code10, '' para_code11, '' para_code12, '' para_code13, '' para_code14, '' para_code15, '' para_code16, '' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
from TF_R_SUPPLY_BILL_DETAIL a
where a.supply_bill_id = :PARA_CODE1
      AND a.eparchy_code = :PARA_CODE2
      AND ( :PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
      AND ( :PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
      AND ( :PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
      AND ( :PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
      AND ( :PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
      AND ( :PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
      AND ( :PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
      AND ( :PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)
      AND ( :PARA_CODE11 IS NOT NULL OR :PARA_CODE11 IS NULL)
      AND ( :PARA_CODE12 IS NOT NULL OR :PARA_CODE12 IS NULL)
      AND ( :PARA_CODE13 IS NOT NULL OR :PARA_CODE13 IS NULL)
      AND ( :PARA_CODE14 IS NOT NULL OR :PARA_CODE14 IS NULL)
      AND ( :PARA_CODE15 IS NOT NULL OR :PARA_CODE15 IS NULL)