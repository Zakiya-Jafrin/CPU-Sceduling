
import java.util.ArrayList;
import java.util.List;

public class Cpu {
	private Process executingProcess;
	private String cpuId;
    private List<String> state=new ArrayList<String>();
    private List<Integer> response=new ArrayList<Integer>();
    private List<String> res=new ArrayList<String>();
	public Cpu(String cpuId) {
		this.cpuId = cpuId;
	}
	
	public int getUtilisedTimeSlots() {
		int i=0;
		for(String s:this.getState()) {
			if(!s.trim().isEmpty()) {
				i++;
			}
		}
		return i;
	}
	
	public void log() {
		this.state.add(executingProcess!=null?executingProcess.getId():"  ");
	}
	
	public void displayState() {
		System.out.println(this.cpuId+":"+this.state);
	}

	public List<String> state() {
		return this.state;
	}
	
	
	public void postTick() {
		if(executingProcess!=null&&!executingProcess.getStatus().isRunning()){
			executingProcess=null;
		}
	}
	
	public boolean isAvailable() {
		return this.executingProcess==null;
	}

	public Process getExecutingProcess() {
		return executingProcess;
	}

	public void setExecutingProcess(Process executingProcess) {
		executingProcess.setStatus(ProcessStatus.RUNNING);
		this.executingProcess = executingProcess;
	}

	public String getCpuId() {
		return cpuId;
	}

	public List<String> getState() {
		return state;
	}
}
