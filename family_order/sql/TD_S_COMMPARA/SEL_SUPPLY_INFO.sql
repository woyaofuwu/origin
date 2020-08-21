SELECT a.supplyid para_code1, ( SELECT d.ordername FROM TF_R_ORDER d WHERE d.orderid = a.orderid ) para_code2, a.stock_id para_code3, ( SELECT b.agencyname FROM TF_R_AGENCY b WHERE b.agencyid = a.agencyid ) para_code4, ( SELECT c.statedesc FROM TD_S_SUPPLYSTATE c WHERE c.supplystate = a.supplystate ) para_code5, to_char(a.supplydate,'yyyy-MM-dd') para_code6, to_char(a.lastsupplydate,'yyyy-MM-dd') para_code7, a.supplystate para_code8, a.orderid para_code9, '' para_code10, '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15, '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name  
FROM TF_R_SUPPLYINFO a
WHERE ( a.supplyid = :PARA_CODE1 OR :PARA_CODE1 IS NULL )
  AND ( a.orderid = :PARA_CODE2 OR :PARA_CODE2 IS NULL )
  AND ( a.supplystate = :PARA_CODE3 OR :PARA_CODE3 IS NULL )
  AND ( a.stock_id = :PARA_CODE4 OR :PARA_CODE4 IS NULL )
  AND ( a.lastsupplydate >= :PARA_CODE5 OR :PARA_CODE5 IS NULL )
  AND ( a.lastsupplydate <= :PARA_CODE6 OR :PARA_CODE6 IS NULL )
  AND ( a.supplydate >= :PARA_CODE7 OR :PARA_CODE7 IS NULL )
  AND ( a.supplydate <= :PARA_CODE8 OR :PARA_CODE8 IS NULL )
  AND ( a.agencyid = :PARA_CODE9 OR :PARA_CODE9 IS NULL )
  AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)