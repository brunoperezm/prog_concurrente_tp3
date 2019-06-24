class Policy {

	public static TasksManager.CPUNumber getCpuBuffer(PN pn) {
		int buffer1Tokens = pn.getPlaceTokens(PN.Places.Buffer1);
		int buffer2Tokens = pn.getPlaceTokens(PN.Places.Buffer2);
		if (buffer1Tokens > buffer2Tokens) return TasksManager.CPUNumber.CPU2;
		else return TasksManager.CPUNumber.CPU1;
	}
}
