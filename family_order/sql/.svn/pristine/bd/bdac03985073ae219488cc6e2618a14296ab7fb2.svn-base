--IS_CACHE=Y
SELECT 'StaffDepartId' KEY, STAFF_ID VALUE1, '-1' VALUE2, d.depart_id VRESULT
  FROM td_m_staff s,td_m_depart d
  WHERE s.depart_id=d.depart_id(+)
    AND 'StaffDepartId' = :KEY