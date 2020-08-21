select c.ibsysid,o.sub_ibsysid from tf_b_eop_subscribe c,tf_b_eop_other o 
where o.ibsysid = o.ibsysid
and c.ibsysid=:IBSYSID
and (
    (o.node_id='infoReview' and o.attr_code='AUDIT_RESULT' and o.attr_value='1') or
    (o.node_id='infoApprove' and o.attr_code='AUDIT_RESULT' and o.attr_value='1' and c.bpm_templet_id='TIMERREVIEWFOURMANAGE') or
    (o.node_id='infoPrvApprove' and o.attr_code='AUDIT_RESULT' and o.attr_value='1')
)