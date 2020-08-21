package com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard;

import java.net.URL;

import org.apache.axis.client.Service;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.alibaba.fastjson.JSON;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSServNode;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSVarNode;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.NeaSoapBindingStub;

public class SoapInputXml {
	   public String receiveMobileRecharge(String orderId, String serlial_number,
				String priority, String switchCode, CWSServNode[] mVServList,
				CWSVarNode[] mVVarList) throws Exception
	    {
		   String Inputxml ="<?xml version="+"\"1.0\""+" encoding="+"\"utf-8\""+"?>"+
				  "<inputData>"+
				     "<orderId>"+
				            orderId  +
			         "</orderId>"+
			         "<serialNumber>"+
			                serlial_number  +
			         "</serialNumber>"+
			         "<priority>"+
			                priority  +
			         "</priority>"+
			         "<switchId>"+
			                switchCode  +
			         "</switchId>"+
			         "<servList>"+
			         "<servName>"+
			                      mVServList[0].getMStrServName()  +
			                "</servName>"+
			         "</servList>"+
			         "<varList>"+
			                "<varName>"+
			                     mVVarList[0].getMStrName()  +
                            "</varName>"+
                            "<varValue>"+
                                 mVVarList[0].getMStrValue()  +
                            "</varValue>"+
			         "</varList>"+
			         "<varList>"+
		                    "<varName>"+
		                         mVVarList[1].getMStrName()  +
                            "</varName>"+
                            "<varValue>"+
                                 mVVarList[1].getMStrValue()  +
                            "</varValue>"+
		             "</varList>"+
		             "<varList>"+
	                        "<varName>"+
	                             mVVarList[2].getMStrName()  +
                            "</varName>"+
                            "<varValue>"+
                                 mVVarList[2].getMStrValue()  +
                            "</varValue>"+
	                 "</varList>"+
	                 "<varList>"+
                            "<varName>"+
                                 mVVarList[3].getMStrName()  +
                            "</varName>"+
                            "<varValue>"+
                                 mVVarList[3].getMStrValue()  +
                            "</varValue>"+
                     "</varList>"+
                     "</inputData>";
		return Inputxml;
	    }
	   
	   public String receiveMobileRecharge_second(String orderId, String serlial_number,
				String priority, String switchCode, CWSServNode[] mVServList,
				CWSVarNode[] mVVarList) throws Exception
	    {
		   String Inputxml ="<?xml version="+"\"1.0\""+" encoding="+"\"utf-8\""+"?>"+
				  "<inputData>"+
				     "<orderId>"+
				            orderId  +
			         "</orderId>"+
			         "<serialNumber>"+
			                serlial_number  +
			         "</serialNumber>"+
			         "<priority>"+
			                priority  +
			         "</priority>"+
			         "<switchId>"+
			                switchCode  +
			         "</switchId>"+
			         "<servList>"+
			                "<servName>"+
			                      mVServList[0].getMStrServName()  +
			                "</servName>"+
			         "</servList>"+
			         "<varList>"+
			                "<varName>"+
			                     mVVarList[0].getMStrName()  +
                           "</varName>"+
                           "<varValue>"+
                                mVVarList[0].getMStrValue()  +
                           "</varValue>"+
			         "</varList>"+
			         "<varList>"+
		                    "<varName>"+
		                         mVVarList[1].getMStrName()  +
                           "</varName>"+
                           "<varValue>"+
                                mVVarList[1].getMStrValue()  +
                           "</varValue>"+
		             "</varList>"+
		             "<varList>"+
	                        "<varName>"+
	                             mVVarList[2].getMStrName()  +
                           "</varName>"+
                           "<varValue>"+
                                mVVarList[2].getMStrValue()  +
                           "</varValue>"+
	                 "</varList>"+
                    "</inputData>";
		return Inputxml;
	    }
public static void main(String[] args) throws Exception {
	
	URL u=new URL("http://10.199.48.184:8081");
	NeaSoapBindingStub n=new NeaSoapBindingStub(u,new Service());
	String s=n.callWSSOP("---");
	System.out.println(s);
}

}
