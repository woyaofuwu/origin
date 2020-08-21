UPDATE tl_b_custinfoqry_operlog a
  SET a.audit_flag=:AUDIT_FLAG
  ,a.audit_time=SYSDATE
  ,a.audit_staff_id=:AUDIT_STAFF_ID
  ,a.audit_depart_id=:AUDIT_DEPART_ID
WHERE a.oper_id=:OPER_ID
  AND a.audit_flag=:OLD_AUDIT_FLAG