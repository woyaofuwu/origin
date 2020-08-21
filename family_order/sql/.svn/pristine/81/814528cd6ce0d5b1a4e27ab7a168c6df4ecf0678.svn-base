select * from
(SELECT d.discnt_name element_name,
       t.package_id,
       t.element_type_code,
       t.element_id,
       t.default_tag,
       t.force_tag,
       t.enable_tag,
       to_char(t.start_absolute_date,
               'yyyy-mm-dd') start_absolute_date,
       t.start_offset,
       t.start_unit,
       t.end_enable_tag,
       to_char(t.end_absolute_date,
               'yyyy-mm-dd') end_absolute_date,
       t.end_offset,
       t.end_unit,
       to_char(t.start_date,
               'yyyy-mm-dd') start_date,
       to_char(t.end_date,
               'yyyy-mm-dd') end_date,
       t.item_index,
       to_char(t.update_time,
               'yyyy-mm-dd hh24:mi:ss') update_time,
       t.update_staff_id,
       t.update_depart_id,
       t.remark,
       t.rsrv_str3,
       t.CANCEL_TAG,
       t.rsrv_tag1,
       d.DISCNT_EXPLAIN ELEMENT_EXPLAIN,
       d.remark param_name      
  FROM td_b_package_element t,td_b_discnt d
 WHERE
 t.element_id = d.discnt_code
 and  t.element_type_code = 'D'
   AND t.package_id = :PACKAGE_ID
   and (d.eparchy_code='ZZZZ' or d.eparchy_code=:EPARCHY_CODE)
	 AND SYSDATE BETWEEN t.start_date AND t.end_date
	 and SYSDATE BETWEEN d.start_date AND d.end_date
and (:TRADE_STAFF_ID='SUPERUSR' or exists
 (select 1
    from (select b.data_code
			from tf_m_staffdataright a, tf_m_roledataright b
			where
			a.data_type = 'D'
			and a.right_attr = 1
			and a.right_tag = 1
			and a.data_code = b.role_code
			and a.staff_id = :TRADE_STAFF_ID
			union
			select a.data_code
			from tf_m_staffdataright a
			where
			a.data_type = 'D'
			and a.right_attr = 0
			and a.right_tag = 1
			and a.staff_id =:TRADE_STAFF_ID) tmp2
   where tmp2.data_code = to_char(t.element_id)))
	 union
 SELECT d.discnt_name element_name,
       t.package_id,
       t.element_type_code,
       t.element_id,
       t.default_tag,
       t.force_tag,
       t.enable_tag,
       to_char(t.start_absolute_date,
               'yyyy-mm-dd') start_absolute_date,
       t.start_offset,
       t.start_unit,
       t.end_enable_tag,
       to_char(t.end_absolute_date,
               'yyyy-mm-dd') end_absolute_date,
       t.end_offset,
       t.end_unit,
       to_char(t.start_date,
               'yyyy-mm-dd') start_date,
       to_char(t.end_date,
               'yyyy-mm-dd') end_date,
       t.item_index,
       to_char(t.update_time,
               'yyyy-mm-dd hh24:mi:ss') update_time,
       t.update_staff_id,
       t.update_depart_id,
       t.remark,
       t.rsrv_str3,
       t.CANCEL_TAG,
       t.rsrv_tag1,
       d.DISCNT_EXPLAIN ELEMENT_EXPLAIN,
       d.remark param_name      
  FROM td_b_package_element t,td_b_discnt d,tf_f_user_discnt ud
 WHERE
 t.element_id = ud.discnt_code
 and d.discnt_code = ud.discnt_code
 and ud.package_id = :PACKAGE_ID
 and ud.user_id = :USER_ID
 and  t.element_type_code = 'D'
   AND t.package_id = :PACKAGE_ID
	 and ud.end_date > sysdate) tmp1
	  ORDER BY tmp1.ELEMENT_NAME