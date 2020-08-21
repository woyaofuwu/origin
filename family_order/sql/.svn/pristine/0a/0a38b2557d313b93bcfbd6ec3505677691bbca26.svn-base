update td_oh_blackuser_sms
      set 
       END_TIME         = sysdate,      
       remark           = :REMARK,
       update_time      = SYSDATE,
       upate_staff      = :UPATE_STAFF,
       update_depart    = :UPDATE_DEPART
 WHERE init_serinum = :INIT_SERINUM
  and END_TIME>sysdate