Declare
    iv_serial_number  VARCHAR2(15) := :SERIAL_NUMBER;
    iv_user_id      NUMBER(16) := TO_NUMBER(:USER_ID);
BEGIN
    :CODE:= -1;
    :INFO:= 'TRADE OK!';
    
    IF TRIM(iv_serial_number) IS NOT NULL AND TRIM(iv_user_id) IS NOT NULL THEN   
       Begin
            UPDATE tf_f_user SET serial_number=iv_serial_number
               WHERE user_id=iv_user_id AND remove_tag='0';
       Exception
         WHEN OTHERS THEN
        :CODE := -1;
        :INFO:='更新用户主表serial_number字段出错'||substr(sqlerrm,1,150);
        RETURN;
        END;
       
       Begin
            UPDATE Tf_f_User_Infochange SET serial_number=iv_serial_number
               WHERE user_id=iv_user_id AND  partition_id = mod(iv_user_id, 10000)　and end_date>sysdate;
       Exception
         WHEN OTHERS THEN
        :CODE := -1;
        :INFO:='更新用户Infochange表serial_number字段出错'||substr(sqlerrm,1,150);
        RETURN;
        END;
 
       Begin
            UPDATE tf_f_user_grpmbmp SET serial_number=iv_serial_number,update_time=sysdate 
               WHERE user_id=iv_user_id AND  partition_id = mod(iv_user_id, 10000)　and end_date>sysdate;
       Exception
         WHEN OTHERS THEN
        :CODE := -1;
        :INFO:='更新tf_f_user_grpmbmp表serial_number字段出错'||substr(sqlerrm,1,150);
        RETURN;
        END;
        
       Begin
            UPDATE tf_f_user_grpmbmp_sub SET serial_number=iv_serial_number,update_time=sysdate 
               WHERE user_id=iv_user_id AND  partition_id = mod(iv_user_id, 10000)　and end_date>sysdate;
       Exception
         WHEN OTHERS THEN
        :CODE := -1;
        :INFO:='更新tf_f_user_grpmbmp_sub表serial_number字段出错'||substr(sqlerrm,1,150);
        RETURN;
        END;
        
    END IF;
    :CODE:= 0;
END;