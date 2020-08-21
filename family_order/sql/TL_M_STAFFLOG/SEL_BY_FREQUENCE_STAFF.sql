select count(1),STAFF_ID
from TL_M_STAFFLOG
where to_char(IN_TIME, 'YYYY-MM-DD') = :DATE
and staff_id=:STAFF_ID
and to_number((OUT_TIME-IN_TIME)*24*60) < 1 
group by STAFF_ID
having count(1) > to_number(:FREQUENCE)