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

class FetchThread implements Runnable
{
    public boolean done = false;
    private Luwrain luwrain;
    private Strings strings;
    private Area messageArea;

    public FetchThread(Luwrain luwrain,
		       Strings strings,
		       Area messageArea)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	this.messageArea = messageArea;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
	if (messageArea == null)
	    throw new NullPointerException("messageArea may not be null");
    }

    private void fetchNews()
    {
	final Object o = luwrain.getSharedObject("luwrain.pim.news");
	if (o == null || !(o instanceof NewsStoring))
	{
	    message(strings.noNewsGroupsData());
	    return;
	}
	NewsStoring newsStoring = (NewsStoring)o;
	newsStoring = (NewsStoring)newsStoring.clone();
	if (newsStoring == null)
	{
	    message(strings.noNewsGroupsData());
	    return;
	}
	StoredNewsGroup[] groups;
	try {
	    groups = newsStoring.loadNewsGroups();
	}
	catch (Exception e)
	{
	    message(strings.newsGroupsError());
	    Log.error("fetch", "the problem while getting list of news groups:" + e.getMessage());
	    e.printStackTrace();
	    return;
	}
	if (groups == null || groups.length < 1)
	{
	    message(strings.noNewsGroups());
	    return;
	}
	for(StoredNewsGroup g: groups)
	{
	    try {
		fetchNewsGroup(newsStoring, g);
	    }
	    catch (Exception e)
	    {
		message(strings.newsFetchingError(g.getName()));
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
	    message(strings.newsGroupFetched(group.getName(), freshNews.size(), totalCount));
    }

    @Override public void run()
    {
	done = false;
	fetchNews();
	message(strings.fetchingCompleted());
	done = true;
    }

    private void message(String text)
    {
	if (text != null && !text.trim().isEmpty())
	    luwrain.enqueueEvent(new MessageLineEvent(messageArea, text));
    }
}
