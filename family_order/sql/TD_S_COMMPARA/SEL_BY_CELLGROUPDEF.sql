--IS_CACHE=Y
SELECT a.cellgroup_id para_code1,a.msc para_code2,
a.lac para_code3,
a.cell_id para_code4, a.begin_time para_code5,
a.end_time para_code6, a.area_code para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,a.remark remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM ucr_param.td_cellgroup_def a
WHERE (a.cellgroup_id = :PARA_CODE1 OR :PARA_CODE1 IS NULL)
  AND (a.cell_id = :PARA_CODE4 OR :PARA_CODE4 IS NULL)
  AND (a.lac = :PARA_CODE3 OR :PARA_CODE3 IS NULL)