Select a.city_code,a.staff_id,a.staff_name,b.depart_code,b.depart_name
  From TD_M_STAFF a, TD_M_DEPART b
Where a.depart_id = b.depart_id
  And a.staff_id = :STAFF_ID
  And a.dimission_tag = '0'
  And b.validflag = '0'