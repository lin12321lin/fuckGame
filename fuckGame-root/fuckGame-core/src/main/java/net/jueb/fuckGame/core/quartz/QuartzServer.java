/**
 * 2013-8-21下午5:18:58
 * @author wang
 */
package net.jueb.fuckGame.core.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wang
 * 
 */
public class QuartzServer {

	protected final Logger _log = LoggerFactory.getLogger(this.getClass());

	private Scheduler sched;

	public QuartzServer() {
		SchedulerFactory sf = new StdSchedulerFactory();
		try {
			sched = sf.getScheduler();
			sched.start();
		} catch (Exception ex) {
			_log.error("sched", ex);
		}
	}

	public void setSched(Scheduler sched) {
		this.sched = sched;
	}

	public void addJob(QuartzTask task) throws Exception {
		JobDetail jobDetail = newJob(task.getClass()).withIdentity(task.getName(), task.getGroup()).build();
		Trigger trigger = newTrigger().withIdentity(task.getName() + "trigger", task.getGroup())
				.withSchedule(cronSchedule(task.getCrond())).build();
		sched.scheduleJob(jobDetail, trigger);
	}
}
