class Policy {

	public static TasksManager.CPUNumber getCpuBuffer(PN pn) {
		int buffer1Tokens = pn.getPlaceTokens(PN.Places.Buffer1);
		int buffer2Tokens = pn.getPlaceTokens(PN.Places.Buffer2);

		 return (buffer1Tokens > buffer2Tokens) ? TasksManager.CPUNumber.CPU2 : TasksManager.CPUNumber.CPU1;
	}
}
