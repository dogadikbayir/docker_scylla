package de.hpi.bpt.scylla.plugin.batch;

import java.util.Collection;
import java.util.Set;

import de.hpi.bpt.scylla.simulation.ProcessInstance;
import de.hpi.bpt.scylla.simulation.ResourceObject;
import de.hpi.bpt.scylla.simulation.ResourceObjectTuple;
import de.hpi.bpt.scylla.simulation.SimulationModel;
import de.hpi.bpt.scylla.simulation.event.ScyllaEvent;
import de.hpi.bpt.scylla.simulation.event.TaskBeginEvent;
import desmoj.core.simulator.TimeInstant;

public class BatchStashResourceEvent extends ScyllaEvent {

	private ResourceObjectTuple resources;
	private BatchCluster cluster;
	private boolean resourcesInStash = false;

	public BatchStashResourceEvent(BatchCluster cluster, TaskBeginEvent taskBeginEvent, ResourceObjectTuple resources) {
		super(cluster.getModel(), cluster.getName()+"_stashNode#"+taskBeginEvent.getNodeId(), new TimeInstant(0), cluster.getProcessSimulationComponents(), taskBeginEvent.getProcessInstance(), taskBeginEvent.getNodeId());
		this.resources = resources;
		this.cluster = cluster;
	}

	@Override
	public void eventRoutine(ProcessInstance processInstance) {
	}
	
	public void makeResourcesUnavailable() {
		//Remove resources from queues; they are not available anymore
		SimulationModel model = (SimulationModel)getModel();
		for(ResourceObject resource : resources.getResourceObjects()) {
			Collection<ResourceObject> typeQueue = model.getResourceManager().availableResources(resource.getResourceType());
			boolean removed = typeQueue.remove(resource);
			assert removed;
		}
        processInstance.getAssignedResources().put(source, resources);
        setResourcesInStash(true);
	}
	
	/**
	 * Have the resources for the old source been removed?
	 * @param resourceIds : Ids of released resources
	 * @return
	 */
	public boolean interestedInResources(Set<String> resourceIds) {
		return resources.getResourceObjects().stream().anyMatch((any)->{return resourceIds.contains(any.getResourceType());})
				&& resourcesFree();
		//return !processInstance.getAssignedResources().containsKey(taskSource);
	}
	
	public boolean resourcesFree() {
		return resources.getResourceObjects().stream().allMatch((each)->{
			Collection<ResourceObject> typeQueue = ((SimulationModel)getModel()).getResourceManager().availableResources(each.getResourceType());
			return typeQueue.contains(each);
		});
	}
	
	public ResourceObjectTuple getResources() {
		return resources;
	}
	
	public BatchCluster getCluster() {
		return cluster;
	}

	public boolean areResourcesInStash()/**or are they assigned in any way?*/ {
		return resourcesInStash;
	}

	public void setResourcesInStash(boolean resourcesInStash) {
		this.resourcesInStash = resourcesInStash;
	}

}
