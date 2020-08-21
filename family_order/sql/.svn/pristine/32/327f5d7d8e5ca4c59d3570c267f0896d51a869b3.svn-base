SELECT a.supply_bill_id para_code1, a.supply_bill_code para_code2, a.bill_status para_code3, decode( a.bill_status, '0', '生成', '1', '处理中', '2', '结束', '3', '中止', '未知状态' ) para_code4, to_char( a.apply_time, 'yyyy-mm-dd hh24:mi:ss' ) para_code5, decode( a.audit_flag, '0', '未审核', '1', '审核通过', '2', '审核未通过', '未知状态' ) para_code6, to_char( a.audit_time, 'yyyy-mm-dd hh24:mi:ss' ) para_code7, ( select FACTORY from TD_M_RES_FACTORY where eparchy_code = a.eparchy_code and FACTORY_CODE = a.FACTORY_CODE and res_type_code = '4') para_code8, b.area_name para_code9, c.depart_name para_code10, ( select staff_name from td_m_staff where staff_id = a.apply_staff_id ) para_code11, ( select staff_name from td_m_staff where staff_id = a.audit_staff_id ) para_code12, a.remark para_code13, a.audit_remark para_code14, to_char( ( select sum(supply_num) from TF_R_SUPPLY_BILL_DETAIL where supply_bill_id = a.supply_bill_id and eparchy_code = a.eparchy_code ) ) para_code15, '' para_code16, '' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM  TF_R_SUPPLY_BILL a, td_m_area b, td_m_depart c
WHERE a.eparchy_code = :PARA_CODE1
      AND a.supply_chl_code = :PARA_CODE2
      AND a.acpt_depart_id = c.depart_id
      AND c.area_code = b.area_code
      AND (a.supply_bill_id = :PARA_CODE3 OR :PARA_CODE3 IS NULL)
      AND (a.supply_bill_code = :PARA_CODE4  OR :PARA_CODE4 IS NULL)
      AND (a.bill_status = :PARA_CODE5  OR :PARA_CODE5 IS NULL)
      AND (a.audit_flag = :PARA_CODE6  OR :PARA_CODE6 IS NULL)
      AND (a.apply_time >= to_date( :PARA_CODE7, 'yyyy-mm-dd' ) OR :PARA_CODE7 IS NULL)
      AND (a.apply_time <= to_date( :PARA_CODE8, 'yyyy-mm-dd' ) OR :PARA_CODE8 IS NULL)
      AND (a.audit_time >= to_date( :PARA_CODE9 , 'yyyy-mm-dd' ) OR :PARA_CODE9 IS NULL)
      AND (a.audit_time <= to_date( :PARA_CODE10, 'yyyy-mm-dd' ) OR :PARA_CODE10 IS NULL)
      AND (a.factory_code = :PARA_CODE11 OR :PARA_CODE11 IS NULL)
      AND (b.area_code = :PARA_CODE12 OR :PARA_CODE12 IS NULL)
      AND (a.acpt_depart_id = :PARA_CODE13 OR :PARA_CODE13 IS NULL)
      AND ( :PARA_CODE14 IS NOT NULL OR :PARA_CODE14 IS NULL)
      AND ( :PARA_CODE15 IS NOT NULL OR :PARA_CODE15 IS NULL)