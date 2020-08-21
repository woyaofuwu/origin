update tf_m_stafffuncright
set right_tag=:RIGHT_TAG
where staff_id in 
  (select staff_id from td_m_staff where depart_id=:AGENT_ID and DIMISSION_TAG='0')