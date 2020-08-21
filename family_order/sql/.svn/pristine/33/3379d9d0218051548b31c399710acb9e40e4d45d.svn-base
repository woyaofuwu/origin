select a.group_id,a.cust_name,a.manager_staff_id 
   from tf_f_cust_group a,tf_f_cust_groupmember b 
   where a.group_id=b.group_id
   and a.cust_id=b.cust_id 
   and a.remove_tag = '0'
   and b.remove_tag = '0'
   AND b.serial_number=:SERIAL_NUMBER 
   AND NOT EXISTS
     ( select 1 from tf_f_group_import c where a.group_id = c.group_id)
union all
select a.group_id,a.cust_name,a.manager_staff_id 
   from tf_f_cust_group a,tf_f_cust_groupmember b,tf_f_group_import c
   where a.group_id=b.group_id
   and a.cust_id=b.cust_id 
   and c.group_id = b.group_id
   and a.remove_tag = '0'
   and b.remove_tag = '0'
   AND b.serial_number=:SERIAL_NUMBER 
   AND 
   (
       'SUPERUSR' = :MANAGER_STAFF_ID
       or
       c.manager_staff_id = :MANAGER_STAFF_ID
       or 
       (
          c.manager_staff_id<>:MANAGER_STAFF_ID
          and c.depart_id in
          ( 
             select d.depart_id
               from td_m_depart d
              where d.depart_frame like
             (select depart_frame from td_m_depart e where e.depart_id=:DEPART_ID)||'%')
          and 
          ( 
            EXISTS (SELECT 1
        	             FROM tf_m_staffdataright s
        	             WHERE s.right_attr='0'
        	             AND s.right_tag='1'
                       AND s.right_class = '2'
        	             AND s.data_type='1'
        	             AND s.data_code='SYS_CMS_CUSTINFOQUERY'
        	             AND s.staff_id=:MANAGER_STAFF_ID
                       AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1')
				               )
             OR EXISTS (SELECT 1
                      	FROM tf_m_staffdataright s,tf_m_roledataright r
                      	WHERE s.right_attr='1'
                      	  AND s.right_tag='1'
                      	  AND s.data_code=r.role_code
                      	  AND s.data_type='1'
                      	  AND r.data_code='SYS_CMS_CUSTINFOQUERY'
                          and r.right_class = '2'
                      	  AND NOT EXISTS(SELECT 1 FROM tf_m_staffdataright
                      			          WHERE staff_id=s.staff_id
                      			            AND data_code=r.data_code
                      			            AND data_type=r.data_type)
                      	  AND s.staff_id=:MANAGER_STAFF_ID
                          AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1')
                        )
          )
       )
   )