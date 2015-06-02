
package org.luwrain.app.fetch;

import java.util.*;

import org.luwrain.core.Application;
import org.luwrain.core.Shortcut;
import org.luwrain.core.Command;
import org.luwrain.core.CommandEnvironment;
import org.luwrain.core.Worker;
import org.luwrain.core.SharedObject;
import org.luwrain.core.I18nExtension;
import org.luwrain.core.Luwrain;
import org.luwrain.core.Registry;

public class Extension extends org.luwrain.core.extensions.EmptyExtension
{
    @Override public Command[] getCommands(Luwrain luwrain)
    {
	Command[] res = new Command[1];
	res[0] = new Command(){
		@Override public String getName()
		{
		    return "fetch";
		}
		@Override public void onCommand(Luwrain luwrain)
		{
		    luwrain.launchApp("fetch");
		}
	    };
	return res;
    }

    @Override public Shortcut[] getShortcuts(Luwrain luwrain)
    {
	Shortcut[] res = new Shortcut[1];
	res[0] = new Shortcut() {
		@Override public String getName()
		{
		    return "fetch";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    Application[] res = new Application[1];
		    res[0] = new FetchApp();
		    return res;
		}
	    };
	return res;
    }

    @Override public void i18nExtension(Luwrain luwrain, I18nExtension i18nExt)
    {
	i18nExt.addCommandTitle("en", "fetch", "News fetching");
	i18nExt.addCommandTitle("ru", "fetch", "Доставка новостей");
	i18nExt.addStrings("ru", "luwrain.fetch", new org.luwrain.app.fetch.i18n.Ru());
    }
}
