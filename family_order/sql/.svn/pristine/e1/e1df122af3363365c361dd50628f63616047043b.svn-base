select sgb.*, woa.sub_ibsysid
  from tf_b_eop_subscribe sgb, tf_b_eop_other woa
 where sgb.bpm_templet_id = 'VOICEMANAGE'
   and sgb.ibsysid = woa.ibsysid
   and woa.node_id = 'infoPrvApprove'
   and woa.attr_code = 'AUDIT_RESULT'
   and woa.attr_value = '1'
   and (:IBSYSID is null or sgb.ibsysid = :IBSYSID)
   AND (:CITY_CODE is null or sgb.eparchy_code = :CITY_CODE)
 order by sgb.ibsysid desc