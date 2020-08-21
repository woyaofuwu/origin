SELECT a.orderid PARA_CODE1, a.agencyid PARA_CODE2, a.ordername PARA_CODE3, b.agencyname PARA_CODE4, a.orderstate PARA_CODE5, to_char(createdate,'yyyy-mm-dd') PARA_CODE6, '' para_code7, '' para_code8, '' para_code9, '' para_code10, '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15, '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM TF_R_ORDER a, TF_R_AGENCY b
WHERE a.agencyid = b.agencyid
      AND ( a.agencyid = :PARA_CODE1 OR :PARA_CODE1 IS NULL )
      AND ( a.orderid = :PARA_CODE2 OR :PARA_CODE2 IS NULL )
      AND ( a.ordername = :PARA_CODE3 OR :PARA_CODE3 IS NULL )
      AND ( b.agencyName like '%'||:PARA_CODE4||'%' OR :PARA_CODE4 IS NULL )  
      AND ( a.createDate>=to_date( :PARA_CODE5, 'yyyy-mm-dd' ) OR :PARA_CODE5 IS NULL )
      AND ( a.createDate<=to_date( :PARA_CODE6,'yyyy-mm-dd')+1 OR :PARA_CODE6 IS NULL )
      AND ( ( :PARA_CODE7 = 'available'  and a.orderState!='3' and a.orderState!='4' ) OR a.orderState = :PARA_CODE7 OR :PARA_CODE7 IS NULL )
      AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
      AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
      AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)