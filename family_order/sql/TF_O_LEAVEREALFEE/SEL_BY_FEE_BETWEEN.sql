select partition_id,to_char(user_id) user_id,to_char(leave_real_fee) leave_real_fee,to_char(real_fee) real_fee,in_tag,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
       from tf_o_leaverealfee where leave_real_fee >=to_number(:FEE1) and leave_real_fee <=to_number(:FEE2) 
       and substr(user_id,1,2)=substr(:EPARCHY_CODE,3,2)