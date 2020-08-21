SELECT decode(substr(U.USER_STATE_CODESET,1,1),'0','00','1','02','2','02','3','02','4','02',
'5','02','7','02','6','04','8','03','9','04','A','01','B','01','E','04','F','03','G','01',
'I','02','J','02','K','02','L','02','M','02','N','00','O','02','P','02','Q','02','05') AS USER_STATE_CODESET FROM TF_F_USER U WHERE U.USER_ID = :USER_ID 
AND u.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)