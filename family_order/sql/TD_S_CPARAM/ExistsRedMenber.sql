select COUNT(*) recordcount
from hn_sms_redmember
where serial_number=:SERIAL_NUMBER
and end_time>sysdate