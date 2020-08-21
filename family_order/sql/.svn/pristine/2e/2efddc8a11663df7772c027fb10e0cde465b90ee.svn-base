--IS_CACHE=Y
SELECT staff_id,a.depart_id,staff_name ,
f_sys_getcodename('depart_id',a.depart_id,null,null) depart_name
  FROM td_m_staff a,td_m_depart c
 WHERE a.depart_id = c.depart_id 
       and c.depart_frame like (
           select t.depart_frame from td_m_depart t where t.depart_id= :DEPART_ID)||'%'
   AND dimission_tag=:DIMISSION_TAG order by c.order_no,staff_id