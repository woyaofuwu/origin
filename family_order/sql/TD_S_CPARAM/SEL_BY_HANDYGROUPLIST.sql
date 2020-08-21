SELECT  COUNT(*) recordcount
FROM tf_f_cust_group
WHERE remove_tag='0'
AND cust_name like '%'||:CUST_NAME||'%'
AND group_status='0'
AND (cust_manager_id=:MANAGER_ID OR :MANAGER_ID IS NULL)
AND cust_manager_id IN (SELECT staff_id FROM td_m_staff
						WHERE depart_id IN (SELECT depart_id FROM td_m_depart
						                    WHERE (depart_id=:DEPARTID OR :DEPARTID IS NULL)
						                           AND (:STAFF_ID IS NULL OR depart_frame
						                                LIKE (SELECT b.depart_frame||'%' FROM td_m_staff a,td_m_depart b
						                                      WHERE a.staff_id=:STAFF_ID
						                                      AND b.depart_id=a.depart_id))))