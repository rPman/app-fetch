
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

public class Extension implements org.luwrain.core.Extension
{
    @Override public String init(String[] args, Registry registry)
    {
	return null;
    }

    @Override public Command[] getCommands(CommandEnvironment env)
    {
	Command[] res = new Command[1];
	res[0] = new Command(){
		@Override public String getName()
		{
		    return "fetch";
		}
		@Override public void onCommand(CommandEnvironment env)
		{
		    env.launchApp("fetch");
		}
	    };
	return res;
    }

    @Override public Shortcut[] getShortcuts()
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

    @Override public Worker[] getWorkers()
    {
	return new Worker[0];
    }

    @Override public SharedObject[] getSharedObjects()
    {
	return new SharedObject[0];
    }

    @Override public void i18nExtension(I18nExtension i18nExt)
    {
	i18nExt.addCommandTitle("en", "fetch", "News fetching");
	i18nExt.addCommandTitle("ru", "fetch", "Доставка новостей");
	i18nExt.addStrings("ru", "luwrain.fetch", new org.luwrain.app.fetch.i18n.Ru());
    }

    @Override public org.luwrain.mainmenu.Item[] getMainMenuItems(CommandEnvironment env)
    {
	return new org.luwrain.mainmenu.Item[0];
    }

    @Override public void close()
    {
	//Nothing here;
    }
}
