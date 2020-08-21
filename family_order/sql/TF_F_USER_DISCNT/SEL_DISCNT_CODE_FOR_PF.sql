select  a.discnt_code 
from Tf_f_User_Discnt a   ,TD_B_DTYPE_DISCNT b
Where user_id=:USER_ID
And a.partition_id=Mod(:USER_ID,10000) 
And a.discnt_code=b.discnt_code And b.discnt_type_code='R'
And a.end_date>Sysdate