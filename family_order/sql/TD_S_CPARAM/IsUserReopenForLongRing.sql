select count(1) recordcount
from Tf_f_User
where user_id_b=:USER_ID
  and open_mode='1'