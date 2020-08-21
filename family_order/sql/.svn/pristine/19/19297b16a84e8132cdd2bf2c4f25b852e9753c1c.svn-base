select c.area_name para_code1, b.depart_name para_code2, e.namebrand_desc para_code3, f.device_model para_code4, decode(a.color_code, null, '不区分颜色', g.color) para_code5, h.factory para_code6, to_char(count(a.device_id ) ) para_code7, b.area_code para_code8, a.stock_id para_code9, d.namebrand_code para_code10, a.device_model_code para_code11, a.color_code para_code12, a.factory_code para_code13, '' para_code14, '' para_code15, '' para_code16, '' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
from TF_R_MOBILEDEVICE a join TD_M_DEPART b on a.stock_id = b.depart_id join TD_M_AREA c on b.area_code = c.area_code join TF_R_MOBILEDEVICE_APPEND d on a.device_id = d.device_id join TD_S_NAMEBRAND e on d.namebrand_code = e.namebrand_code join TD_S_DEVICE_MODEL f on a.device_model_code = f.device_model_code and f.eparchy_code = a.eparchy_code and f.device_type_code='0' join TD_S_DEVICE_COLOR g on a.color_code = g.color_code and g.eparchy_code = a.eparchy_code and f.device_type_code='0' join TD_M_RES_FACTORY h on a.Factory_Code = h.factory_code and h.eparchy_code = a.eparchy_code and h.res_type_code='4'
where a.device_type_code='0'
      and a.eparchy_code = :PARA_CODE1
      and d.supply_chl_code = :PARA_CODE2
      and ( b.area_code = :PARA_CODE3 or :PARA_CODE3 is null )
      and ( a.stock_id = :PARA_CODE4 or :PARA_CODE4 is null )
      and ( d.namebrand_code = :PARA_CODE5 or :PARA_CODE5 is null )
      and ( a.device_model_code = :PARA_CODE6 or :PARA_CODE6 is null )
      and ( a.color_code = :PARA_CODE7 or :PARA_CODE7 is null )
      AND ( :PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)
      AND ( :PARA_CODE11 IS NOT NULL OR :PARA_CODE11 IS NULL)
      AND ( :PARA_CODE12 IS NOT NULL OR :PARA_CODE12 IS NULL)
      AND ( :PARA_CODE13 IS NOT NULL OR :PARA_CODE13 IS NULL)
      AND ( :PARA_CODE14 IS NOT NULL OR :PARA_CODE14 IS NULL)
      AND ( :PARA_CODE15 IS NOT NULL OR :PARA_CODE15 IS NULL)
group by c.area_name, b.depart_name, e.namebrand_desc, f.device_model,
       decode(a.color_code, null, '不区分颜色', g.color), h.factory,
       b.area_code, a.stock_id, d.namebrand_code, a.device_model_code, a.color_code, a.factory_code
having count(a.device_id ) >= to_number(:PARA_CODE8)
       and count(a.device_id ) <= to_number(:PARA_CODE9)