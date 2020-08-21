select d.area_name para_code1, c.depart_name para_code2, decode(a.sale_lease_flag, '1', '售', '租') para_code3, h.purchase_name para_code4, i.namebrand_desc para_code5, j.device_model para_code6, to_char(count(b.device_id)) para_code7, c.area_code para_code8, a.sale_depart_id para_code9, a.sale_lease_flag para_code10, g.lease_type_code para_code11, g.namebrand_code para_code12, f.device_model_code para_code13,'' para_code14, '' para_code15, '' para_code16, '' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
from TF_R_SALE_LEASE a join TF_R_SALE_LEASE_DTL b on a.sale_lease_id = b.sale_lease_id join TD_M_DEPART c on a.sale_depart_id = c.depart_id and c.validflag = '0' join TD_M_AREA d on c.area_code = d.area_code and d.validflag = '0' join TF_R_MOBILEDEVICE f on b.device_id = f.device_id and f.eparchy_code = a.eparchy_code and f.device_type_code='0' join TF_R_MOBILEDEVICE_APPEND g on b.device_id = g.device_id and g.supply_type = '0' join TD_B_PURCHASETYPE h on g.lease_type_code = h.purchase_mode and h.eparchy_code = a.eparchy_code join TD_S_NAMEBRAND i on g.namebrand_code = i.namebrand_code join TD_S_DEVICE_MODEL j on f.device_model_code = j.device_model_code 
where a.eparchy_code = :PARA_CODE1
      and g.supply_chl_code = :PARA_CODE2
      AND ( c.area_code = :PARA_CODE3 OR :PARA_CODE3 IS NULL)
      AND ( a.sale_depart_id = :PARA_CODE4 OR :PARA_CODE4 IS NULL)
      AND ( a.sale_lease_flag = :PARA_CODE5 OR :PARA_CODE5 IS NULL)
      AND ( g.lease_type_code = :PARA_CODE6 OR :PARA_CODE6 IS NULL)
      AND ( g.namebrand_code = :PARA_CODE7 OR :PARA_CODE7 IS NULL)
      AND ( f.device_model_code = :PARA_CODE8 OR :PARA_CODE8 IS NULL)
      and ( a.sale_time >= to_date(:PARA_CODE9 ,'yyyy-mm-dd') OR :PARA_CODE9 IS NULL )
      and ( a.sale_time <= to_date(:PARA_CODE10 ,'yyyy-mm-dd') OR :PARA_CODE10 IS NULL )
      AND ( :PARA_CODE11 IS NOT NULL OR :PARA_CODE11 IS NULL)
      AND ( :PARA_CODE12 IS NOT NULL OR :PARA_CODE12 IS NULL)
      AND ( :PARA_CODE13 IS NOT NULL OR :PARA_CODE13 IS NULL)
      AND ( :PARA_CODE14 IS NOT NULL OR :PARA_CODE14 IS NULL)
      AND ( :PARA_CODE15 IS NOT NULL OR :PARA_CODE15 IS NULL)
group by d.area_name, c.depart_name, decode(a.sale_lease_flag, '1', '售', '租'), h.purchase_name,
       i.namebrand_desc, j.device_model, 
       c.area_code, a.sale_depart_id, a.sale_lease_flag, g.lease_type_code, g.namebrand_code,
       f.device_model_code