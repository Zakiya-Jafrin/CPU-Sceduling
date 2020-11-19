
import java.util.ArrayList;
import java.util.List;

public class Process {
	public static final int IO_TIME_UNIT = 2;
	private String id;
	private long arrivalTime;
	private long totalExecutionTime;
	private List<Long> ioTimes = new ArrayList<Long>();
	private long executionTimeCounter = 0;
	private long ioTimeCounter = 0;
	private long nextIoSwitchTime = 0;
	private long currentIoOperationTime = 0;
	private long currentExecutionOperationTime = 0;
	private ProcessStatus status = ProcessStatus.NOTREADY;
	
	private long actualArrival=1000000;
	private long actualCompletion=-1;
	
	public Process(String processInfo) {
		String[] splitString = processInfo.split("\\s+");
		this.id = splitString[0];
		this.arrivalTime = Long.valueOf(splitString[1]);
		this.totalExecutionTime = Long.valueOf(splitString[2]);
		if (splitString.length > 3) {
			for (long i = 3; i < splitString.length; i++) {
				ioTimes.add(Long.valueOf(splitString[(int)i]));
			}
			nextIoSwitchTime = ioTimes.get(0);
		}
	}

	public void postTick() {
		if (!this.getStatus().isComplete()&&!this.getStatus().isNotReady()) {
			if (this.getStatus().isBlocked()) {
				this.currentIoOperationTime++;
				this.ioTimeCounter++;
			} else if (this.status.isRunning()) {
				this.executionTimeCounter++;
				this.currentExecutionOperationTime++;
			}
			// Check if ready for IO
			if (!this.status.isBlocked() && this.executionTimeCounter == this.nextIoSwitchTime) {
				setStatus(ProcessStatus.BLOCKED);
				this.currentExecutionOperationTime=0;
				this.nextIoSwitchTime = 0;
				for (Long ioTime : ioTimes) {
					if (ioTime > this.executionTimeCounter) {
						this.nextIoSwitchTime = ioTime;
						break;
					}
				}
			}
			// Check if ready for Complete
			if (this.executionTimeCounter >= this.totalExecutionTime) {
				setStatus(ProcessStatus.COMPLETE);
			}
			if (this.status.isBlocked()&&this.currentIoOperationTime>=IO_TIME_UNIT) {
				this.status=ProcessStatus.WAITING;
				this.currentIoOperationTime=0;
			}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(long arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public long getTotalExecutionTime() {
		return totalExecutionTime;
	}

	public void setTotalExecutionTime(long totalExecutionTime) {
		this.totalExecutionTime = totalExecutionTime;
	}

	public List<Long> getIoTimes() {
		return ioTimes;
	}

	public void setIoTimes(List<Long> ioTimes) {
		this.ioTimes = ioTimes;
	}

	public long getExecutionTimeCounter() {
		return executionTimeCounter;
	}

	public void setExecutionTimeCounter(long executionTimeCounter) {
		this.executionTimeCounter = executionTimeCounter;
	}

	public long getNextIoSwitchTime() {
		return nextIoSwitchTime;
	}

	public void setNextIoSwitchTime(long nextIoSwitchTime) {
		this.nextIoSwitchTime = nextIoSwitchTime;
	}

	public long getCurrentIoOperationTime() {
		return currentIoOperationTime;
	}

	public void setCurrentIoOperationTime(long currentIoOperationTime) {
		this.currentIoOperationTime = currentIoOperationTime;
	}

	public ProcessStatus getStatus() {
		return status;
	}

	public void setStatus(ProcessStatus status) {
		this.status = status;
	}
	
	public long getTimeLeftForExecution() {
		return this.totalExecutionTime-this.executionTimeCounter;
	}

	public long getCurrentExecutionOperationTime() {
		return currentExecutionOperationTime;
	}

	public void setCurrentExecutionOperationTime(long currentExecutionOperationTime) {
		this.currentExecutionOperationTime = currentExecutionOperationTime;
	}

	public long getIoTimeCounter() {
		return ioTimeCounter;
	}

	public void setIoTimeCounter(long ioTimeCounter) {
		this.ioTimeCounter = ioTimeCounter;
	}

	public long getActualArrival() {
		return actualArrival;
	}

	public void setActualArrival(long actualArrival) {
		this.actualArrival = actualArrival;
	}

	public long getActualCompletion() {
		return actualCompletion;
	}

	public void setActualCompletion(long actualCompletion) {
		this.actualCompletion = actualCompletion;
	}

	@Override
	public String toString() {
		return "Process [id=" + id + ", arrivalTime=" + arrivalTime + ", totalExecutionTime=" + totalExecutionTime
				+ ", ioTimes=" + ioTimes + ", executionTimeCounter=" + executionTimeCounter + ", ioTimeCounter="
				+ ioTimeCounter + ", nextIoSwitchTime=" + nextIoSwitchTime + ", currentIoOperationTime="
				+ currentIoOperationTime + ", currentExecutionOperationTime=" + currentExecutionOperationTime
				+ ", status=" + status + ", actualArrival=" + actualArrival + ", actualCompletion=" + actualCompletion
				+ "]";
	}

}
