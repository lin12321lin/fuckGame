package net.jueb.fuckGame.core.quartz;

import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class QuartzTask implements Job {
	
	protected final Logger _log = LoggerFactory.getLogger(this.getClass());

	public abstract String getName();

	public abstract String getGroup();

	public abstract String getCrond();
}
