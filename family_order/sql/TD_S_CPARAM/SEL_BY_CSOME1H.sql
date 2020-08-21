select count(*) recordcount
FROM tf_fh_cust_contract a
 WHERE (contract_id like '%'||:PARAM0||'%' or :PARAM0 is null)
   AND (contract_name like '%'||:PARAM5||'%' or :PARAM5 is null)
   AND (rsrv_str2>=:PARAM6 or :PARAM6 IS NULL)
   AND backmonth=:PARAM7
   AND a.cust_id in
         ( select b.cust_id
	   from tf_fh_cust_group b,td_m_staff d,td_m_depart e,tf_fh_group_order c
	   where b.manager_staff_id = d.staff_id and b.group_id=c.group_id
	   and d.depart_id = e.depart_id
	   and ( c.prevaluec1 (
		  select product_name from td_b_grp_product
		  start with product_name = :PARAM2
		  connect by prior product_name = brand_code
		)  or :PARAM2 is null )
	   and b.remove_tag = '0' and b.backmonth=:PARAM7 and c.backmonth=:PARAM7
	   and ( b.manager_staff_id = :PARAM3 OR :PARAM3 IS NULL)
           and (b.group_id like '%'||:PARAM1||'%' or :PARAM1 IS NULL)
	   and e.depart_frame like (
               select t.depart_frame from td_m_depart t where t.depart_id= :PARAM4)||'%')