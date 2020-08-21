select count(1) recordcount
from tf_f_cust_vip a
   WHERE remove_tag= :PARAM8
    AND (a.cust_manager_id=:PARAM0 or :PARAM0 is null)
	   AND (a.vip_type_code=:PARAM1 or :PARAM1 is null)
	   AND (a.class_id=:PARAM2 or :PARAM2 is null)
	   AND (a.serial_number=:PARAM3 or :PARAM3 is null)
	   AND (a.vip_no=:PARAM4 or :PARAM4 is null)
	   AND a.destroy_date>=TO_DATE(:PARAM5 , 'YYYY-MM-DD HH24:MI:SS')
	   AND a.destroy_date<=TO_DATE(:PARAM6 , 'YYYY-MM-DD HH24:MI:SS')
       AND EXISTS
	        ( SELECT 1
	          FROM  td_m_staff b,td_m_depart c
	          WHERE a.cust_manager_id = b.staff_id
	          and   b.depart_id = c.depart_id
	          and c.depart_frame like (
	             select t.depart_frame from td_m_depart t where t.depart_id= :PARAM7)||'%'
	         )