--IS_CACHE=Y
select right_code from TF_M_STAFFFUNCRIGHT 
where right_attr='1' and staff_id=:STAFF_ID and right_tag='1'