package org.matrix.framework.core.platform.scheduler;

import java.util.Date;
import org.matrix.framework.core.util.TimeLength;

public abstract interface Scheduler {
    public abstract void triggerOnce(Job paramJob);

    public abstract void triggerOnceAt(Job paramJob, Date paramDate);

    public abstract void triggerOnceWithDelay(Job paramJob, TimeLength paramTimeLength);
}