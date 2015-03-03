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

import org.luwrain.core.*;

public class FetchApp implements Application, Actions
{
    private Luwrain luwrain;
    private Strings strings;
    private FetchArea fetchArea;
    private FetchThread fetchThread ;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	final Object o = luwrain.i18n().getStrings("luwrain.fetch");
	if (o == null || !(o instanceof Strings))
	    return false;
	this.luwrain = luwrain;
	strings = (Strings)o;
	fetchArea = new FetchArea(luwrain, this, strings);
	return true;
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public void launchFetching()
    {
	if (fetchThread != null && !fetchThread.done)
	{
	    luwrain.message(strings.processAlreadyRunning(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	fetchArea.clear();
	fetchThread = new FetchThread(luwrain, strings, fetchArea);
	new Thread(fetchThread).start();
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(fetchArea);
    }

    @Override public void close()
    {
	if (fetchThread != null && !fetchThread.done)
	{
	    luwrain.message(strings.processNotFinished(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	luwrain.closeApp();
    }
}
