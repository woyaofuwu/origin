SELECT COUNT(1) recordcount
from tf_f_relation_uu
where user_id_a=to_number(:USER_ID_A)
    and relation_type_code=:RELATION_TYPE_CODE
    and (short_code=:SHORT_CODE OR :SHORT_CODE='ZZZZ')
    and end_date>sysdate