SELECT group_id,cust_name,cust_manager_id manager_staff_id
FROM tf_f_cust_group
WHERE ROWID IN (SELECT rid
				FROM (SELECT ROWNUM rn,rid
					  FROM(SELECT ROWID rid,group_id
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
						   ORDER BY group_id)
					  WHERE rownum<=TO_NUMBER(:PAGE)*TO_NUMBER(:PAGE_SIZE))
				WHERE rn>(TO_NUMBER(:PAGE)-1)*TO_NUMBER(:PAGE_SIZE))
ORDER BY group_id