SELECT a.supplyid PARA_CODE1, decode(a.auditresult,'0','审核通过','1','审核未通过') PARA_CODE2, ( SELECT staff_name FROM TD_M_STAFF WHERE staff_id = a.auditoptrid ) PARA_CODE3, to_char(a.audittime,'yyyy-MM-dd hh24:mi:ss') PARA_CODE4, a.auditremark PARA_CODE5, '' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10, '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15, '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM TF_R_SUPPLYAUDIT a
WHERE a.supplyid = :PARA_CODE1
      AND (:PARA_CODE2 IS NOT NULL OR :PARA_CODE2 IS NULL)
      AND (:PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
      AND (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
      AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
      AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
      AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
      AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
      AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
      AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)
ORDER BY a.audittime ASC