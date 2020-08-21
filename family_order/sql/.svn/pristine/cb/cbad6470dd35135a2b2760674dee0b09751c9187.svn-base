--IS_CACHE=Y
select (select area_name from td_m_area where area_code = :PARA_CODE2) para_code1,b.depart_id para_code2, b.depart_code para_code3, b.depart_name para_code4, b.depart_kind_code para_code5, b.area_code para_code6, c.area_name para_code7, c.area_level para_code8,(SELECT right_class FROM tf_m_staffdataright WHERE staff_id = a.staff_id AND right_attr = '0' and rownum = 1) para_code9, ( select code_type_code from td_m_departkind where depart_kind_code = b.depart_kind_code and eparchy_code = :PARA_CODE2 ) para_code10,'' para_code11, '' para_code12, '' para_code13, '' para_code14, '' para_code15, '' para_code16, '' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
from td_m_staff a, td_m_depart b, td_m_area c
where a.staff_id = :PARA_CODE1
    and a.depart_id = b.depart_id
    and c.area_code = b.area_code
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