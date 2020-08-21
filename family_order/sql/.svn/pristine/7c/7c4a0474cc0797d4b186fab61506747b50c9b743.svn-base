select t.asyn_id,
       t.accept_month,
       t.attr_code,
       t.attr_value,
       t.remark,
       t.valid_tag,
       t.eparchy_code,
       t.accept_depart_id,
       t.update_depart_id,
       t.update_staff_id,
       t.accept_staff_id,
       TO_CHAR(T.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE,
       TO_CHAR(T.UPDATE_DATE, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE,
       t.ATTR_TYPE,
	   t.ATTR_ID,
       t.PARENT_ATTR_ID,
       t.ATTR_GROUP
  from tf_b_ewe_asyn_attr t
  where t.asyn_id = :ASYN_ID
