SELECT t.* FROM TF_F_USER_ALTSN t where STATUS = :STATUS
                               and  RELA_TYPE = :RELA_TYPE and add_months( t.activate_time , :INTERVAL ) < sysdate