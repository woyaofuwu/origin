select a.device_id para_code1, e.color para_code2, f.factory para_code3, to_char(b.sale_time, 'yyyy-mm-dd hh24:mi:ss') para_code4, '' para_code5, '' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10, '' para_code11, '' para_code12, '' para_code13, '' para_code14, '' para_code15, '' para_code16, '' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
from TF_R_SALE_LEASE_DTL a join TF_R_SALE_LEASE b on a.sale_lease_id = b.sale_lease_id join TF_R_MOBILEDEVICE c on a.device_id = c.device_id and c.eparchy_code = b.eparchy_code join TF_R_MOBILEDEVICE_APPEND d on a.device_id = d.device_id and d.supply_type = '0' join TD_S_DEVICE_COLOR e on c.color_code = e.color_code and e.device_type_code = '0' and e.eparchy_code = b.eparchy_code join TD_M_RES_FACTORY f on c.factory_code = f.factory_code and f.eparchy_code = b.eparchy_code and f.res_type_code = '4' join TD_M_DEPART g on b.sale_depart_id = g.depart_id and g.validflag = '0'
where b.eparchy_code = :PARA_CODE1
      and d.supply_chl_code = :PARA_CODE2
      and ( g.area_code = :PARA_CODE3 or :PARA_CODE3 is null )
      and ( b.sale_depart_id = :PARA_CODE4 or :PARA_CODE4 is null )
      and ( b.sale_lease_flag = :PARA_CODE5 or :PARA_CODE5 is null )
      and ( d.lease_type_code = :PARA_CODE6 or :PARA_CODE6 is null )
      and ( d.namebrand_code = :PARA_CODE7 or :PARA_CODE7 is null )
      and ( c.device_model_code = :PARA_CODE8 or :PARA_CODE8 is null )
      and ( b.sale_time >= to_char( :PARA_CODE9, 'yyyy-mm-dd hh24:mi:ss') or :PARA_CODE9 is null )
      and ( b.sale_time <= to_char( :PARA_CODE10, 'yyyy-mm-dd hh24:mi:ss') or :PARA_CODE10 is null )
      AND ( :PARA_CODE11 IS NOT NULL OR :PARA_CODE11 IS NULL)
      AND ( :PARA_CODE12 IS NOT NULL OR :PARA_CODE12 IS NULL)
      AND ( :PARA_CODE13 IS NOT NULL OR :PARA_CODE13 IS NULL)
      AND ( :PARA_CODE14 IS NOT NULL OR :PARA_CODE14 IS NULL)
      AND ( :PARA_CODE15 IS NOT NULL OR :PARA_CODE15 IS NULL)