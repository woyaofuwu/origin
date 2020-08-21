SELECT cparam_key, cparam_name, cparam_type, refresh_interval, last_time, to_char(timestamp,'yyyymmddhh24miss') updated_time, state, state_date 
 FROM td_s_cparam_config 
WHERE cparam_key = :KEY