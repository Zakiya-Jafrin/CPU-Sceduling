

public class Launch {

	public static void main(String[] args) {
		new FcfsScheduler().displayProcess();
		new FcfsScheduler().start();
		new SjbScheduler().start();
		new SrtfScheduler().start();
		new RoundRobinScheduler().start();
	}

}
