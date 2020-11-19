

public enum ProcessStatus {
	NOTREADY, WAITING, RUNNING, BLOCKED, COMPLETE;
	public boolean isNotReady() {
		return this.equals(NOTREADY);
	}

	public boolean isWaiting() {
		return this.equals(WAITING);
	}

	public boolean isRunning() {
		return this.equals(RUNNING);
	}

	public boolean isBlocked() {
		return this.equals(BLOCKED);
	}

	public boolean isComplete() {
		return this.equals(COMPLETE);
	}

}
