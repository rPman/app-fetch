/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.fetch;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.io.*;

import javax.mail.*;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;

import org.luwrain.extensions.pim.*;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;

public class EmailReader
{
	static Properties props=new Properties();
	static Session session=Session.getDefaultInstance(new Properties(), null); // by default was used empty session for working .eml files
	static Store store;
	static Folder folder=null;
	static Message message=null;
	// part of file path for store cache, for example folder directory as is or serialized file as cachePathNameChunk+".ser" 
	static String cachePathNameChunk=null;
	// max number messages count to load from big email folders when first loaded (limit for testing)
	static final int LIMIT_MESSAGES_LOAD=15;
	
	// TODO: remove this crap, used only for sending
	static String login,password;
	
	public static void InitServerConnection(HashMap<String,String> settings,String host,String login,String password) throws Exception
	{
		props.clear();
		for(Map.Entry<String,String> p:settings.entrySet()) props.put(p.getKey(), p.getValue());
		EmailReader.login=login;
		EmailReader.password=password;
		// connect
		session = Session.getInstance(props,null);
		store = session.getStore();
		store.connect(host,login,password);
	}
	public static String[] getFolderNames() throws Exception
	{
		Folder[] folders=store.getDefaultFolder().list();
		String[] result=new String[folders.length];
		for(int i=0;i<folders.length;i++) result[i]=folders[i].getFullName();
		return result;
	}
	// fetch emails from server, folder name can be INBOX or any other IMAP folder name
	// can be limited via receivedDate<lastReceived if it not null
	public static <StoredEmailMessageSql> EmailMessage[] getEmails(String folderName,Date lastReceived) throws Exception
    {
		Vector<EmailMessage> result=new Vector<EmailMessage>();
		// open folder
		if(folder!=null) folder.close(true); // true - remove messages market to remove
		folder = store.getFolder(folderName);
		folder.open(Folder.READ_ONLY);
		// get messages list
		Message[] messages;
		if(lastReceived==null)
		{
			int msgcount=folder.getMessageCount();
			messages=folder.getMessages(Math.max(msgcount-LIMIT_MESSAGES_LOAD+1,1),msgcount);
		} else
		{
			messages=folder.search(new ReceivedDateTerm(ComparisonTerm.GE,lastReceived));	
		}
		// fetch messages
		for(Message message:messages)
		{
			EmailStoringSql es=new EmailStoringSql(); // FIXME: fix class usage for email storing 
			es.setOnlineMessageObject(message);
			es.readJavamailMessageBaseFields();
			es.readJavamailMessageOnline();
			result.add(es);
		}
		
		
		return (EmailMessage[]) result.toArray();
    }
}
