select count(*) recordcount
FROM tf_fh_cust_contract a
 WHERE (contract_id like '%'||:PARAM0||'%' or :PARAM0 is null)
   AND (contract_name like '%'||:PARAM4||'%' or :PARAM4 is null)
   AND (rsrv_str2>=:PARAM5 or :PARAM5 IS NULL)
   AND backmonth=:PARAM6
   AND a.cust_id in
         ( select b.cust_id
	   from tf_fh_cust_group b,td_m_staff d,td_m_depart e
	   where b.manager_staff_id = d.staff_id
	   and d.depart_id = e.depart_id
	   and b.remove_tag = '0' and b.backmonth=:PARAM6
	   and ( b.manager_staff_id = :PARAM2 OR :PARAM2 IS NULL)
           and b.group_id like '%'||:PARAM1||'%'
	   and e.depart_frame like (
               select t.depart_frame from td_m_depart t where t.depart_id= :PARAM3)||'%')