--IS_CACHE=Y
SELECT staff_id  para_code1,depart_id  para_code2,
staff_name  para_code3,job_info para_code4, manager_info para_code5,
decode(sex,'0','男','1','女') para_code6, email para_code7, user_pid para_code8, serial_number para_code9, cust_id para_code10,
decode(dimission_tag,'0','正常','1','已经离职') para_code11,birthday para_code12,staff_group_id para_code13,cust_hobyy para_code14,remark para_code15,
rsvalue1   para_code16,rsvalue2  para_code17,update_time  para_code18,update_staff_id  para_code19,update_depart_id para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM td_m_staff A
WHERE (:PARA_CODE1 IS NULL OR depart_id IN (SELECT depart_id FROM td_m_depart WHERE area_code =: VPARA_CODE1))
   AND (A.staff_id BETWEEN :PARA_CODE2 AND :PARA_CODE3)
   AND A.dimission_tag='0'
   AND :PARA_CODE4 IS NULL
   AND :PARA_CODE5 IS NULL
   AND :PARA_CODE6 IS NULL
   AND :PARA_CODE7 IS NULL
   AND :PARA_CODE8 IS NULL
   AND :PARA_CODE9 IS NULL
   AND :PARA_CODE10 IS NULL
ORDER BY para_code1