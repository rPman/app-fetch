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
    private StringConstructor stringConstructor;

    public FetchArea(Luwrain luwrain,
		     Actions actions,
		     StringConstructor stringConstructor)
    {
	super(new DefaultControlEnvironment(luwrain), stringConstructor.appName());
	this.luwrain = luwrain;
	this.actions = actions;
	this.stringConstructor = stringConstructor;
	addLine(stringConstructor.pressEnterToStart());
	addLine("");
	addLine("");
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && !event.isModified() &&
	    event.getCommand() == KeyboardEvent.ENTER)
	{
	    actions.launchFetching();
	    return true;
	}
	return super.onKeyboardEvent(event);
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch(event.getCode())
	{
	case EnvironmentEvent.THREAD_SYNC:
	    MessageLineEvent messageLineEvent = (MessageLineEvent)event;
	    if (getLineCount() > 1)
		setLine(getLineCount() - 1, messageLineEvent.message); else
		addLine(messageLineEvent.message);
	    addLine("");
	    if (messageLineEvent.message.equals(stringConstructor.fetchingCompleted()))
		luwrain.message(messageLineEvent.message);
	    return true;
	case EnvironmentEvent.CLOSE:
	    actions.close();
	    return true;
	default:
	    return false;
	}
    }
}
