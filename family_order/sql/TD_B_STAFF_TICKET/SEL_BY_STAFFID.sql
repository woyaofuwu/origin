--IS_CACHE=Y
select  STAFF_ID ,TICKET_TYPE_CODE,TICKET_ID,TAX_NO  
from td_b_staff_ticket 
where staff_id =:STAFF_ID
and TICKET_TYPE_CODE=:TICKET_TYPE_CODE