delete from TF_F_USER_GPON_DESTROY
       where  KD_USER_ID = :USER_ID
        and (sysdate<destroy_order_time 
       or to_char(SYSDATE,'YYYYMMDD')=to_char(destroy_order_time,'YYYYMMDD'))