    select distinct(e.bi_sn),e.bi_sn ibsysid,e.city_code,c.group_id,c.cust_name,e.accept_staff_id staff_id,e.create_date INSERT_TIME,e.bpm_templet_id,o.sub_ibsysid
  from tf_b_ewe           e,
       tf_b_eop_other     o,
       tf_b_eop_subscribe c,
       tf_b_ewe_node n
        where e.bpm_templet_id = 'VOICEMANAGE'
   and  n.node_id = 'infoPrvApprove'
   and e.busiform_id = n.busiform_id
   and  o.sub_ibsysid = n.sub_bi_sn
   and o.attr_code = 'AUDIT_RESULT'
   and o.attr_value = '1'
   and c.ibsysid = e.bi_sn
   and c.group_id = :GROUP_ID
   and e.city_code = :CITY_CODE
   and e.bi_sn = :IBSYSID
   Union
      select distinct(e.bi_sn),e.bi_sn ibsysid,e.city_code,c.group_id,c.cust_name,e.accept_staff_id staff_id,e.create_date INSERT_TIME,e.bpm_templet_id,o.sub_ibsysid
  from tf_b_ewe           e,
       tf_b_ewe_node_tra  t,
       tf_b_eop_other     o,
       tf_b_eop_subscribe c
 where e.bpm_templet_id = 'VOICEMANAGE'
   and t.node_id = 'infoPrvApprove'
   and e.busiform_id = t.busiform_id 
   and o.sub_ibsysid = t.sub_bi_sn
   and o.attr_code = 'AUDIT_RESULT'
   and o.attr_value = '1'
   and c.ibsysid = e.bi_sn
   and c.group_id = :GROUP_ID
   and e.city_code = :CITY_CODE
   and e.bi_sn = :IBSYSID