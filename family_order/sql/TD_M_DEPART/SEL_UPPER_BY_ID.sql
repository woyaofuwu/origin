--IS_CACHE=Y
select depart_id,depart_name,depart_frame from td_m_depart where depart_id in (select parent_depart_id from td_m_depart where depart_id = :DEPART_ID)