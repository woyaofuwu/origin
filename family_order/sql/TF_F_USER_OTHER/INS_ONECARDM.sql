begin
    :CODE:= -1;
    :INFO:= 'TRADE OK!';
insert into tf_f_relation_span (user_id_a,serial_number_a,user_id_b,serial_number_b,start_date,end_date)
values(TO_NUMBER(:USER_ID_A),:SERIAL_NUMBER_A,TO_NUMBER(:USER_ID_B),:SERIAL_NUMBER_B,TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'));
   :CODE:= 0;
end;