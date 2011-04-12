package ds.android.tasknet.config;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import ds.android.tasknet.task.Task;

/**
 *
 * @author Divya_PKV
 */
public class Node implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    String nodeName;
    Integer nodeIndex;
    InetAddress nodeAddress;
    long memoryCapacity;
    float processorLoad;
    Integer batteryLevel;
    Map<String, Task> acceptedTasks = new HashMap<String, Task>();
//    String taskId;
//    Boolean distributed;
//    Integer sequenceNumber;

    Node(String name, int index, InetAddress address) {
        nodeName = name;
        nodeIndex = index;
        nodeAddress = address;
        memoryCapacity = 0;
        processorLoad = 0;
        batteryLevel = 0;
//        distributed = false;
//        sequenceNumber = -1;
    }

//    public void setSeqNum(Integer sNum) {
//        sequenceNumber = sNum;
//    }
//
//    public Integer getSeqNum() {
//        return sequenceNumber;
//    }
//
//    public void setTaskid(String id) {
//        taskId = id;
//    }
//
//    public String getTaskid() {
//        return taskId;
//    }

    public int getIndex() {
        return nodeIndex;
    }

    public String getName() {
        return nodeName;
    }

    public InetAddress getAdrress() {
        return nodeAddress;
    }

    public long getMemoryCapacity() {
        return memoryCapacity;
    }

    public float getProcessorLoad() {
        return processorLoad;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int value) {
//        batteryLevel -= value;
    }

//    public Boolean hasBeenDistributed() {
//        return distributed;
//    }
//
//    public void setDistributed(Boolean flag) {
//        distributed = flag;
//    }

    public void update(long currentRAM, float CPUsage, int currentBatteryLevel) {
        memoryCapacity = currentRAM;
        processorLoad = CPUsage;
        batteryLevel = currentBatteryLevel;
    }

    public void addToAcceptedTask(String taskAdvReplyId, Task task) {
    	if(this.acceptedTasks.get(taskAdvReplyId) == null) {
    		this.acceptedTasks.put(taskAdvReplyId, task);
    	}
    }
    
    public void removeFromAcceptedTask(String taskId) {
    	this.acceptedTasks.remove(taskId);
    }
    
    @Override
    public String toString() {
        String str = "";
        str += "Name: " + nodeName;
        str += "\nIndex: " + nodeIndex;
        str += "\nAdrress: " + nodeAddress;
        str += "\nMemory Capacity: " + memoryCapacity;
        str += "\nProcessor Load: " + processorLoad;
        str += "\nBattery Level: " + batteryLevel;
        str += "\n";
        return str;
    }
}
