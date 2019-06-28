class Policy {

	PN mPN;

	public Policy(PN PN) {
		mPN = PN;
	}

	public TasksManager.CPUNumber getCpuBuffer() {
		int buffer1Tokens = mPN.getPlaceTokens(PN.Places.Buffer1);
		int buffer2Tokens = mPN.getPlaceTokens(PN.Places.Buffer2);

		 return (buffer1Tokens > buffer2Tokens) ? TasksManager.CPUNumber.CPU2 : TasksManager.CPUNumber.CPU1;
	}
}
