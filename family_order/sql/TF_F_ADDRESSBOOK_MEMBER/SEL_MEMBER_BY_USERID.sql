SELECT * FROM TF_F_ADDRESSBOOK_MEMBER T 
WHERE 1=1
AND T.USER_ID = :USER_ID 
AND T.REMOVE_TAG = '0'