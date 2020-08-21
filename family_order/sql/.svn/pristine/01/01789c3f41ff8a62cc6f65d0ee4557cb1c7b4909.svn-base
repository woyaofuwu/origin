UPDATE TD_B_RECOMMPARA
set end_date=sysdate,
    UPDATE_TIME = sysdate,
    UPDATE_STAFF_ID = :UPDATE_STAFF_ID,
    UPDATE_DEPART_ID = :UPDATE_DEPART_ID
where recomm_type = :RECOMM_TYPE
and element_id = to_number(:ELEMENT_ID)
and (eparchy_code=:EPARCHY_CODE or eparchy_code='ZZZZ')
and (recomm_source = :RECOMM_SOURCE or :RECOMM_SOURCE is null)
and sysdate BETWEEN start_date AND end_date