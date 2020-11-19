
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SrtfScheduler extends Scheduler{
	@Override
	public void displayHeader() {
		System.out.println("\n===========OUTPUT FOR SRTF=============\n");
	}
	@Override
	public void scheduleProcess() {
		List<Process> processToBeAllocatedList=new ArrayList<>();
		//Pre tick related Task
		for(Process process:processList){
			if(process.getStatus().isNotReady()||process.getStatus().isWaiting()) {
				if(process.getArrivalTime()==tick&&process.getStatus().isNotReady()){
					//Set the process to ready
					process.setStatus(ProcessStatus.WAITING);
				}
				if(process.getStatus().isWaiting()) {
					processToBeAllocatedList.add(process);
				}
			}
		}
		//See if the process can be allocated to any of the CPUS.
		//Order the process by shortest execution time.
		Collections.sort(processToBeAllocatedList,(o1,o2)->(int)(o1.getTimeLeftForExecution() -o2.getTimeLeftForExecution()));
		for(Process p:processToBeAllocatedList) {
			checkCpuAllocation(p);
		}
	}
	
	public void checkCpuAllocation(Process process) {
		super.checkCpuAllocation(process);
		if(!process.getStatus().isRunning()) {
			//Try the preemptive approach
			for(Cpu cpu:this.cpuList) {
				if(cpu.getExecutingProcess().getTimeLeftForExecution()>process.getTimeLeftForExecution()) {
					cpu.getExecutingProcess().setCurrentExecutionOperationTime(0);
					cpu.getExecutingProcess().setStatus(ProcessStatus.WAITING);
					cpu.setExecutingProcess(process);
					break;
				}
			}
		}
	}
    
}
