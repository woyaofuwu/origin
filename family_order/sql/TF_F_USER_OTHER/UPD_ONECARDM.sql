BEGIN
    :CODE:= -1;
    :INFO:= 'TRADE OK!';
    UPDATE tf_f_relation_span SET end_date=sysdate where serial_number_a=:SERIAL_NUMBER_A
    and serial_number_b=:SERIAL_NUMBER_B;
    :CODE:= 0;
END;