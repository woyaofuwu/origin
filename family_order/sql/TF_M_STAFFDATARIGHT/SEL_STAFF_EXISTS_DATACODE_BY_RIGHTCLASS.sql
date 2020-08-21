--IS_CACHE=Y
SELECT staff_id
FROM 
(
SELECT b.staff_id
FROM tf_m_staffdataright b,tf_m_roledataright c
WHERE 1=1
AND b.staff_id = :STAFF_ID
 AND b.right_tag = '1'                                   
 AND b.right_attr = '1'                              
 AND b.data_code = c.role_code
 AND c.data_type = '1'                              
 AND c.rsvalue1 in('0','2')                        
 AND NVL(c.right_class,'0') >= :RIGHT_CLASS            
 AND c.data_code = :DATA_CODE
UNION ALL
SELECT d.staff_id
FROM tf_m_staffdataright d
WHERE 1=1
AND d.staff_id = :STAFF_ID
AND d.right_tag='1'                                    
AND d.right_attr = '0'                               
AND d.data_type = '1'                             
AND d.rsvalue1 in('0','2')                   
AND NVL(d.right_class,'0') >= :RIGHT_CLASS         
 AND d.data_code = :DATA_CODE
 )