SELECT msg_id para_code1, r_src_id para_code2, r_msg para_code3, r_dest_id para_code4, r_datetime para_code5, accepttime para_code6,acceptmonth para_code7, 
dealstate para_code8, src_id para_code9, dest_id para_code10, s_msg para_code11,busi_lasttime para_code12,s_datetime para_code13,finishtime para_code14,'' para_code15, '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name  
FROM hn_ti_oh_ismg
where r_src_id = :PARA_CODE1
AND accepttime >= to_date(:PARA_CODE2,'yyyy-mm-dd')
AND accepttime <= to_date(:PARA_CODE3,'yyyy-mm-dd')
AND s_msg   NOT LIKE '%密码%'
AND (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)