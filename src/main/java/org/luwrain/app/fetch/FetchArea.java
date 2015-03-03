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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.extensions.pim.*;

class FetchArea extends SimpleArea
{
    private Luwrain luwrain;
    private Actions actions;
    private Strings strings;

    public FetchArea(Luwrain luwrain,
		     Actions actions,
		     Strings strings)
    {
	super(new DefaultControlEnvironment(luwrain), strings.appName());
	this.luwrain = luwrain;
	this.actions = actions;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (actions == null)
	    throw new NullPointerException("actions may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
	addLine(strings.pressEnterToStart());
	addLine("");
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	if (event.isCommand() && !event.isModified() &&
	    event.getCommand() == KeyboardEvent.ENTER)
	{
	    actions.launchFetching();
	    return true;
	}
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	switch(event.getCode())
	{
	case EnvironmentEvent.THREAD_SYNC:
	    MessageLineEvent messageLineEvent = (MessageLineEvent)event;
	    if (getLineCount() > 1)
		setLine(getLineCount() - 1, messageLineEvent.message); else
		addLine(messageLineEvent.message);
	    addLine("");
	    if (messageLineEvent.message.equals(strings.fetchingCompleted()))
		luwrain.message(messageLineEvent.message, Luwrain.MESSAGE_OK);
	    return true;
	case EnvironmentEvent.CLOSE:
	    actions.close();
	    return true;
	default:
	    return super.onEnvironmentEvent(event);
	}
    }
}
