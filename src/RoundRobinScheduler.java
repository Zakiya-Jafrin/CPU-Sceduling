

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public class RoundRobinScheduler extends Scheduler {
	private Deque<Process> rrQeueue=new ArrayDeque<>();
//	protected long ROUND_ROBIN_QUANTUM =0;

	@Override
	public void displayHeader() {
		System.out.println("\n===========OUTPUT FOR RR=============\n");
//		System.out.println("Please enter the time quantum: ");
//		ROUND_ROBIN_QUANTUM = new Scanner(System.in).nextLong();
//		System.out.println(" ");

	}
	
	public void scheduleProcess() {
		for(Process process:processList){
			if(process.getStatus().isNotReady()||process.getStatus().isWaiting()) {
				if(process.getArrivalTime()==tick&&process.getStatus().isNotReady()){
					//Set the process to ready
					process.setStatus(ProcessStatus.WAITING);
				}
				if(process.getStatus().isWaiting()) {
					rrQeueue.addLast(process);
				}
			}
		}
		boolean loop=true;
		while(loop) {
			Process p=rrQeueue.peekFirst();
			if(p!=null) {
				checkCpuAllocation(p);
			}
			
			if(p==null||!p.getStatus().isRunning()) {
				loop=false;
			}
		}
	}
	
	
	public void checkCpuAllocation(Process process) {

		if(process.getStatus().isRunning()) {
			rrQeueue.removeFirst();
			return;
		}
		for(Cpu cpu:this.cpuList) {
			if(cpu.isAvailable()) {
				cpu.setExecutingProcess(rrQeueue.removeFirst());
				return;
			}
		}
		if(!process.getStatus().isRunning()) {
			for(Cpu cpu:this.cpuList) {
				if(!rrQeueue.isEmpty()&&cpu.getExecutingProcess().getCurrentExecutionOperationTime()>=ROUND_ROBIN_QUANTUM) {
					cpu.getExecutingProcess().setCurrentExecutionOperationTime(0);
					cpu.getExecutingProcess().setStatus(ProcessStatus.WAITING);
					cpu.setExecutingProcess(rrQeueue.removeFirst());
				}
			}
		}
	}
}
