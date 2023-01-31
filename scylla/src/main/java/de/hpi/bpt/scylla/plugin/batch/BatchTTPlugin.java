package de.hpi.bpt.scylla.plugin.batch;

import de.hpi.bpt.scylla.exception.ScyllaRuntimeException;
import de.hpi.bpt.scylla.plugin_type.simulation.event.TaskTerminateEventPluggable;
import de.hpi.bpt.scylla.simulation.ProcessInstance;
import de.hpi.bpt.scylla.simulation.event.TaskTerminateEvent;

public class BatchTTPlugin extends TaskTerminateEventPluggable {

    @Override
    public String getName() {
        return BatchPluginUtils.PLUGIN_NAME;
    }

    @Override
    public void eventRoutine(TaskTerminateEvent event, ProcessInstance processInstance) throws ScyllaRuntimeException {
        BatchPluginUtils pluginInstance = BatchPluginUtils.getInstance();
        
        BatchCluster cluster = pluginInstance.getCluster(processInstance);
        if(cluster == null)cluster = pluginInstance.getCluster(event);
        if (cluster != null)cluster.taskTerminateEvent(event);
    }

}
