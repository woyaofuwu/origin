select count(1) recordcount
from Tf_f_Relation_Uu
where user_id_b=:USER_ID
  and relation_type_code='20'
  and end_date>sysdate