SELECT a.serial_number para_code1,
       a.city_code para_code2,d.area_name para_code3,
       a.open_date para_code4,b.develop_staff_id para_code5,b.develop_depart_id para_code6,
       '' para_code7, a.user_id para_code8, '' para_code9, '' para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
       '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM (
          SELECT a.user_id,a.partition_id,a.serial_number,a.city_code,a.open_date
            FROM ucr_crm1.tf_f_user a,ucr_crm1.tf_f_user_discnt c
           WHERE a.open_date >= to_date(:PARA_CODE3,'yyyy-mm-dd')
             AND a.open_date <  to_date(:PARA_CODE4,'yyyy-mm-dd')+1
             AND a.eparchy_code = :PARA_CODE7
             AND a.user_state_codeset = :PARA_CODE1
             AND (a.product_id+0 = :PARA_CODE2 OR :PARA_CODE2 IS NULL)
             AND c.user_id = a.user_id+0
             AND c.partition_id = a.partition_id+0 
             AND c.discnt_code+0 = :PARA_CODE5
             AND c.end_date >= SYSDATE
             AND ROWNUM <= to_number(:PARA_CODE6)     
       ) a,ucr_crm1.tf_f_user_file b,ucr_cen1.td_m_area d
 WHERE :PARA_CODE5 IS NOT NULL
   AND b.user_id = a.user_id+0
   AND b.partition_id = a.partition_id+0
   AND d.area_code = a.city_code
   AND d.area_level = 30
   AND (:PARA_CODE8 IS NULL OR :PARA_CODE8 = '')
   AND (:PARA_CODE9 IS NULL OR :PARA_CODE9 = '')
   AND (:PARA_CODE10 IS NULL OR :PARA_CODE10 = '')