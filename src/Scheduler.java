

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public abstract class Scheduler {
	private static final String FORMAT_MESSAGE = "%s -----%s-----%s--------%s";
	public static final String INPUT_FILE = "input.txt";
	protected static final long ROUND_ROBIN_QUANTUM = 3;
	protected List<Process> processList = new ArrayList<>();
	protected List<Cpu> cpuList = new ArrayList<>();
	protected Map<String, Integer> respo = new HashMap<>();
	protected List<Integer> response = new ArrayList<>();
	protected List<String> test = new ArrayList<>();
	protected List<String> test2 = new ArrayList<>();
	protected long tick = 0;

	public Scheduler() {
		try {
			String content = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));
			String[] fileContentLines = content.split("\\R+");
			for (String line : fileContentLines) {
				if (line.startsWith("numOfCPUs:")) {
					createCpus(line);
				}
				if (line.startsWith("p")) {
					this.processList.add(new Process(line));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract void displayHeader();

	public void displayProcess() {
		System.out.println(String.format(FORMAT_MESSAGE, "ProcessId", "ArrivalTime", "Execution Time", "IO time"));
		for (Process process : this.processList) {
			System.out.println(String.format("%s                 %s            %s                   %s",
					process.getId(), process.getArrivalTime(), process.getTotalExecutionTime(), process.getIoTimes()));
		}
	}

	public void start() {
		boolean run = true;
		while (run) {
			preTick();
			this.cpuList.forEach(c -> c.log());
			tick++;
			postTick();
			run = this.processList.stream().anyMatch(a -> !a.getStatus().isComplete());
		}
		displayHeader();
		for (Cpu cpu : this.cpuList) {
			cpu.displayState();
			test2 =new ArrayList<>();

			for (String a: cpu.state()) {
				if(!test.contains(a) && a.startsWith("p") ) {
					test.add(a);
					response.add(cpu.state().indexOf(a));
					respo.put(a,cpu.state().indexOf(a));
//					System.out.println(respo);
				}
			}

			for (String a: cpu.state()) {

				if(!test2.contains(a) && a.startsWith("p") ) {
					test2.add(a);
					if(respo.get(a)>cpu.state().indexOf(a))
						respo.put(a,cpu.state().indexOf(a));
//					response1.add(cpu.state().indexOf(a));

				}
			}
		}
//		System.out.println(respo);
		computeStats();
	}

	private void computeStats() {
		for (Process p : this.processList) {
			int index = (int) p.getActualArrival();
			int lastIndex = -1;
			for (Cpu cpu : this.cpuList) {
				index = cpu.getState().indexOf(p.getId());
//				if(cpu.getExecutingProcess().getId().equals(p.getId())){
//					test.add(p.getId());
//					System.out.println(p.getId());
//					response.add(index);
//				}
				if (index < p.getActualArrival() && index != -1) {
					p.setActualArrival(index);
				}
				lastIndex = cpu.getState().lastIndexOf(p.getId());
				if (lastIndex > p.getActualCompletion()) {
					p.setActualCompletion(lastIndex);
				}
			}
		}
		System.out.println("Turnaround Time & Wait time :");
		long turnAroundTimeCounter = 0;
		long waitTimeCounter = 0;
		long cpuResponseTimeCounter = 0;

		for (Process p : this.processList) {
			System.out.println("  Process " + p.getId() + " - ");
			int turnAround = ((int) (p.getActualCompletion() - p.getActualArrival()) + (int) 1);
 			int cpuResponse = ((int) (respo.get(p.getId()) - p.getArrivalTime()));
//			int cpuResponse = ((int) (p.getActualCompletion() - p.getArrivalTime()) + (int) 1);
			System.out.println("            Turnaround time : " + turnAround);
			System.out.println("            CPU response time : " + cpuResponse);
//			for (int a: response) {
//				if(a==p.getArrivalTime())
//					System.out.println("            Response time : " + (a-p.getArrivalTime()));
//			}
			System.out.println("            Wait time : "
					+ (turnAround - (p.getTotalExecutionTime() + (int) p.getIoTimes().size() * Process.IO_TIME_UNIT)));
			turnAroundTimeCounter += (long) turnAround;
			waitTimeCounter += (turnAround - (p.getTotalExecutionTime() + (int) p.getIoTimes().size() * Process.IO_TIME_UNIT));
			cpuResponseTimeCounter+=cpuResponse;
		}
		System.out.println("Cpu Utilisation");
		for (Cpu cpu : this.cpuList) {
			System.out.println("  "+cpu.getCpuId() + " - " + (cpu.getUtilisedTimeSlots()==0?0:(((float)cpu.getUtilisedTimeSlots()/cpu.getState().size())*100)));
		}
		System.out.println("Average Turnaround Time : " + turnAroundTimeCounter / this.processList.size());
		System.out.println("Average CPU response Time : " + cpuResponseTimeCounter / this.processList.size());
		System.out.println("Average Wait Time : " + (waitTimeCounter == 0 ? 0 : ((float)waitTimeCounter / this.processList.size())));
		System.out.println("Throughput for total time period : " + (this.processList.size() == 0 ? 0 : ((float)this.processList.size()/this.cpuList.size())));
	}

	public void preTick() {
		// Pre tick related Task
		scheduleProcess();
	}

	public void scheduleProcess() {
		for (Process process : processList) {
			if (process.getStatus().isNotReady() || process.getStatus().isWaiting()) {
				if (process.getArrivalTime() == tick && process.getStatus().isNotReady()) {
					// Set the process to ready
					process.setStatus(ProcessStatus.WAITING);
				}
				if (process.getStatus().isWaiting()) {
					// See if the process can be allocated to any of the CPUS
					checkCpuAllocation(process);
				}
			}
		}
	}

	public void postTick() {
		for (Process process : processList) {
			process.postTick();
		}
		for (Cpu cpu : this.cpuList) {
			cpu.postTick();

		}
	}

	public void checkCpuAllocation(Process process) {
		for (Cpu cpu : this.cpuList) {
			if (cpu.isAvailable()) {
				cpu.setExecutingProcess(process);
				break;
			}
		}
	}

	private void createCpus(String line) {
		int numberOfCpus = Integer.parseInt(line.split("\\s+")[1].trim());
		for (int i = 0; i < numberOfCpus; i++) {
			this.cpuList.add(new Cpu("CPU" + (i + 1)));
		}
	}
}
