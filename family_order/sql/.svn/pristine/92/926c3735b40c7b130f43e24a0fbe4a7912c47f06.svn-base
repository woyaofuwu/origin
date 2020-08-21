--IS_CACHE=Y
select staff.* from TD_S_ECHANNAL_STAFF staff where 1 = 1
  and staff.ID_TYPE = :ID_TYPE
  and staff.ID_VALUE like '%' || :ID_VALUE || '%'
  and staff.ID_NAME like '%' || :ID_NAME || '%'
  and staff.STAFF_TYPE = :STAFF_TYPE
  and staff.STAFF_CLASS = :STAFF_CLASS
  and staff.CHAN_ID = :CHAN_ID
  and staff.EPARCHY_CODE = :EPARCHY_CODE
  order by staff.ID_TYPE, staff.ID_VALUE, staff.ID_NAME