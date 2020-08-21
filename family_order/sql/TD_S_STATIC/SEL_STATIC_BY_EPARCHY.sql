--IS_CACHE=Y
Select Data_Id, Data_Name
  From Td_s_Static
 Where (Type_Id = :TYPE_ID||:EPARCHY_CODE Or Type_Id =:TYPE_ID||'ZZZZ') 
 Order By Data_Id
