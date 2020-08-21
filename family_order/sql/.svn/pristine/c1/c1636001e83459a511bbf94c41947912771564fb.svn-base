UPDATE TF_F_USER_OCMSTW
       SET OPR_STATE = :OPR_STATE, END_DATE = to_date(:END_DATE,'YYYY-MM-DD hh24:mi:ss') 
     WHERE USER_ID = :USER_ID
       AND BOSS_SEQ = :BOSS_SEQ
       AND sysdate between start_date and end_date
       and OPR_STATE ='01'