select a.device_id para_code1, a.batch_id para_code2, to_char( a.time_in, 'yyyy-mm-dd hh24:mi:ss' ) para_code3, '' para_code4, '' para_code5, '' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10, '' para_code11, '' para_code12, '' para_code13, '' para_code14, '' para_code15, '' para_code16, '' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
from TF_R_MOBILEDEVICE a
join TF_R_MOBILEDEVICE_APPEND b
on a.device_id = b.device_id
join TD_M_DEPART c
on a.stock_id = c.depart_id
where a.device_type_code='0'
      AND a.eparchy_code = :PARA_CODE1
      AND (a.stock_id =  :PARA_CODE2 or :PARA_CODE2 is null)
      AND (c.area_code = :PARA_CODE3 or :PARA_CODE3 is null)
      AND b.supply_chl_code = :PARA_CODE4
      AND (b.namebrand_code = :PARA_CODE5 or :PARA_CODE5 is null)
      AND (a.device_model_code = :PARA_CODE6 or :PARA_CODE6 is null)
      AND (a.color_code = :PARA_CODE7 or :PARA_CODE7 is null)
      AND ( :PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
      AND ( :PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
      AND ( :PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)
      AND ( :PARA_CODE11 IS NOT NULL OR :PARA_CODE11 IS NULL)
      AND ( :PARA_CODE12 IS NOT NULL OR :PARA_CODE12 IS NULL)
      AND ( :PARA_CODE13 IS NOT NULL OR :PARA_CODE13 IS NULL)
      AND ( :PARA_CODE14 IS NOT NULL OR :PARA_CODE14 IS NULL)
      AND ( :PARA_CODE15 IS NOT NULL OR :PARA_CODE15 IS NULL)