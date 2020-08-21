SELECT a.* FROM TF_F_RELATION_UU a where 
  a.serial_number_b <> :SERIAL_NUMBER and a.relation_type_code='CP'
and a.User_Id_a=(
select t.User_Id_a from TF_F_RELATION_UU t where t.serial_number_b=:SERIAL_NUMBER and t.relation_type_code='CP')