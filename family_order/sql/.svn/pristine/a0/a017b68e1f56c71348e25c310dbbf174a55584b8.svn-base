SELECT a.execute_id,a.execute_desc,a.serial_number,a.id_type,a.id,a.priority,a.description,
       TO_CHAR(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,TO_CHAR(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       a.eparchy_code,TO_CHAR(a.update_date,'yyyy-mm-dd hh24:mi:ss') update_date,a.update_tag,a.update_staff_id,
       a.accept_month,a.rsrv_str1,a.rsrv_str2
  FROM tf_f_recommend a
 WHERE a.serial_number = :SERIAL_NUMBER
   AND SYSDATE BETWEEN a.start_date+0 AND a.end_date+0
   AND NOT EXISTS (SELECT 1 FROM tf_f_recommend_result b
                    WHERE b.serial_number = :SERIAL_NUMBER
                      AND b.execute_id = a.execute_id
                      AND b.deal_tag = '2')
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_svc c
                    WHERE c.user_id = :USER_ID
                      AND a.id_type = '1'
                      AND a.id = c.service_id+0
                      AND c.end_date+0 >= SYSDATE)
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_discnt d
                    WHERE d.user_id = :USER_ID
                      AND a.id_type = '2'
                      AND a.id = d.discnt_code+0
                      AND d.end_date+0 >= SYSDATE)
   AND NOT EXISTS (SELECT 1 FROM ucr_crm1.tf_f_user_mbmp_sub e
                    WHERE e.user_id = :USER_ID 
                      AND a.id_type = '4'
                      AND e.sp_id = '801234' 
                      AND e.sp_svc_id = '110301' 
                      AND e.biz_state_code = 'A'
                      AND e.end_date >= SYSDATE)