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

import java.net.*;
import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.extensions.pim.*;
//import org.luwrain.network.*;

class FetchThread implements Runnable
{
    public boolean done = false;
    private Luwrain luwrain;
    private StringConstructor stringConstructor;
    private Area messageArea;

    public FetchThread(Luwrain luwrain,
		       StringConstructor stringConstructor,
		       Area messageArea)
    {
	this.stringConstructor = stringConstructor;
	this.messageArea = messageArea;
    }

    private void fetchNews()
    {
	NewsStoring newsStoring = null;//luwrain.getPimManager().getNewsStoring();//FIXME:In independent connection;
	if (newsStoring == null)
	{
	    Log.error("fetch", "No news storing object");
	    message(stringConstructor.noNewsGroupsData());
	    return;
	}
	StoredNewsGroup[] groups;
	try {
	    groups = newsStoring.loadNewsGroups();
	}
	catch (Exception e)
	{
	    message(stringConstructor.newsGroupsError());
	    Log.error("fetch", "the problem while getting list of news groups:" + e.getMessage());
	    e.printStackTrace();
	    return;
	}
	if (groups == null || groups.length < 1)
	{
	    message(stringConstructor.noNewsGroups());
	    return;
	}
	for(StoredNewsGroup g: groups)
	{
	    try {
		fetchNewsGroup(newsStoring, g);
	    }
	    catch (Exception e)
	    {
		message(stringConstructor.newsFetchingError(g.getName()));
		Log.error("fetch", "the problem while fetching and saving news in group \'" + g.getName() + "\':" + e.getMessage());
		e.printStackTrace();
	    }
	}
    }

    private void 		fetchNewsGroup(NewsStoring newsStoring, StoredNewsGroup group) throws Exception
    {
	Vector<NewsArticle> freshNews = new Vector<NewsArticle>();
	int totalCount = 0;
	String[] urls = group.getUrls();
	for (int k = 0;k < urls.length;k++)
	{
	    NewsArticle[] articles = FeedReader.readFeed(new URL(urls[k]));
	    totalCount += articles.length;
	    for(NewsArticle a: articles)
		if (newsStoring. countArticlesByUriInGroup(group, a.uri) == 0)
		    freshNews.add(a);
	}
	for(int k = 0;k < freshNews.size();k++)
	    newsStoring.saveNewsArticle(group, freshNews.get(k));
	if (freshNews.size() > 0 )
	    message(stringConstructor.newsGroupFetched(group.getName(), freshNews.size(), totalCount));
    }

    public void run()
    {
	done = false;
	fetchNews();
	message(stringConstructor.fetchingCompleted());
	done = true;
    }

    private void message(String text)
    {
	if (text != null && !text.trim().isEmpty())
	    luwrain.enqueueEvent(new MessageLineEvent(messageArea, text));
    }
}
