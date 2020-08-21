SELECT COUNT(1) recordcount FROM tf_fh_cust_group a,
	       (
		  SELECT group_id,COUNT(1) FROM tf_fh_group_order
		  GROUP BY group_id
		  HAVING COUNT(*)> :PARAM20 ) c
WHERE a.group_id = c.group_id
   AND backmonth=:PARAM19
   AND a.group_id like '%'||:PARAM0||'%'
   and remove_tag = '0'
   AND (cust_name like '%'||:PARAM1||'%'  OR :PARAM1 IS NULL)
   AND (class_id=:PARAM2 OR :PARAM2 IS NULL)
   AND (client_status=:PARAM3 OR :PARAM3 IS NULL)
   AND (company_address  like '%'||:PARAM4||'%' OR :PARAM4 IS NULL)
   AND (enterprise_scope=:PARAM5 OR :PARAM5 IS NULL)
   AND (calling_type_code=:PARAM6 OR :PARAM6 IS NULL)
   AND (calling_sub_type_code=:PARAM7
         OR :PARAM7 IS NULL)
   AND (enterprise_type_code=:PARAM8
          OR :PARAM8 IS NULL)
   AND (enterprise_size_code=:PARAM9
         OR :PARAM9 IS NULL)
   AND (prevaluen1>=:PARAM10 OR :PARAM10 IS NULL)
   AND (super_group_id=:PARAM11 OR :PARAM11 IS NULL)
   AND (manager_staff_id=:PARAM12 OR :PARAM12 IS NULL)
   and (subclass_id=:PARAM13 or :PARAM13 IS NULL)
   and (group_attr=:PARAM14 or :PARAM14 IS NULL)
   and ( decode(rsrv_1,'1','0','0','1','') = :PARAM21 or :PARAM21 is null)
   AND
    ((exists
     ( select 1
       from tf_f_user b,tf_f_user_vpmn c
       WHERE b.user_id = c.user_id
       and a.cust_id = b.cust_id
       AND (serial_number = :PARAM15 OR :PARAM15 IS NULL)
      )
   )  or :PARAM15 is null )
   and
     ((exists
     ( select 1
       from tf_f_group_order d
       where a.group_id = d.group_id
       and (d.prevaluec1 in
                 ( select product_name from td_b_grp_product
		   start with product_name = :PARAM16
		   connect by prior product_name = brand_code
		  )  OR :PARAM16 IS NULL)
       and (d.finish_date > sysdate or d.finish_date is null)
      )
    ) or :PARAM16 is null )
   and
      (
         (exists
	      ( select 1
		from td_m_staff e,td_m_depart f
		where a.manager_staff_id = e.staff_id
		and e.depart_id = f.depart_id
		and f.depart_frame like (
		   select t.depart_frame from td_m_depart t where t.depart_id= :PARAM17)||'%'
		 and (f.parent_depart_id = :PARAM18 OR :PARAM18 IS NULL )
	       )
	 ) or :PARAM17 = '49999' )